(ns bookstore.db.migrate-old
  "Used simply to migrate the old books that we had in MongoDB. Will be deleted afterwards..."
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [clojure.java.shell :refer :all]
            [clojure.spec.alpha :as s]
            [bookstore.db.data.books :as books-db])
  (:import (java.time LocalDate LocalDateTime)
           (java.time.format DateTimeFormatter)
           (java.util Locale UUID)))

(s/def :books.book/title string?)
(s/def :books.book/language (s/nilable string?))
(s/def :books.book/amazon-book-image-front (s/nilable string?))
(s/def :books.book/price (s/nilable number?))
(s/def :books.book/isbn-10 (s/nilable string?))
(s/def :books.book/isbn-13 (s/nilable string?))
(s/def :books.book/asin (s/nilable string?))
(s/def :books.book/book-length (s/nilable number?))
(s/def :books.book/publish-date (s/nilable (partial instance? LocalDate)))
(s/def :books.book/publisher (s/nilable string?))
(s/def :books.book/description (s/nilable string?))
(s/def :books.book/variation (s/nilable string?))
(s/def :books.book/item-added-date (partial instance? LocalDate))
(s/def :books.book/item-added-date-precision (s/nilable #{"day" "month" "year"}))
(s/def :books.book/authors (s/coll-of (s/nilable string?)))

(s/def :books.book/book
  (s/keys
    :req [:books.book/title]
    :opt []
    :opt [:books.book/language
          :books.book/amazon-book-image-front
          :books.book/price
          :books.book/isbn-10
          :books.book/isbn-13
          :books.book/asin
          :books.book/book-length
          :books.book/publish-date
          :books.book/publisher
          :books.book/description
          ; Note that variation is slightly wrong. It should be edition instead
          :books.book/variation
          :books.book/item-added-date
          :books.book/item-added-date-precision
          :books.book/authors]))

(def old-books
  (read-string
    (slurp "mongo/src/main/resources/books_template.clj")))


(defn update-price [price]
  (if (empty? price)
    nil
    (Integer/parseInt (str/replace price #"\ €|," ""))))

(defn update-book-length [book-length]
  (if (empty? book-length)
    nil
    (Integer/parseInt (str/replace book-length #"[Seiten ]" ""))))

(defn parse-date [date]
  (if (empty? date)
    nil
    (let [formatter (.. DateTimeFormatter (DateTimeFormatter/ofPattern "d. MMMM yyyy" Locale/GERMAN))]
      (LocalDate/parse date formatter))))

(defn update-date [date]
  (parse-date (subs date 20)))

(defn get-filename [url]
  (if (empty? url)
    nil
    (subs url (+ 1 (str/last-index-of url "/")))))

(def amazon-regex #".*\/dp\/(?<asin>[A-Z0-9]+)\/.*")
(defn update-asin [amazon-url]
  (if (nil? amazon-url)
    nil
    (let [matcher (re-matcher amazon-regex amazon-url)]
      (if (.matches matcher)
        (.group matcher "asin")
        nil))))

(def release-date-regex #".*\((?<releaseDate>.+)\)")
(defn get-release-date [publisher-line]
  (if (empty? publisher-line)
    nil
    (let [matcher (re-matcher release-date-regex publisher-line)]
      (if (.matches matcher)
        (.group matcher "releaseDate")))))


(def publisher-regex #"(?<publisherAndVariation>.+)(\(.+\))")
(->> (map :books.book/publisher old-books)
     (filter (comp not nil?))
     (map #(let [matcher (re-matcher publisher-regex %)]
             (if (.matches matcher)
               (.group matcher "publisherAndVariation"))))
     (map str/trim)
     (map #(if (str/includes? % ";")
             {:publisher (subs % 0 (str/index-of % ";"))
              :variation (subs % (+ 2 (str/index-of % ";")))}
             {:publisher %})))

(defn get-publisher-and-variation [publisher-line]
  (if-let [matcher (if (some? publisher-line) (re-matcher publisher-regex publisher-line))]
    (if (.matches matcher)
      (str/trim (.group matcher "publisherAndVariation")))))

(defn get-book-variation [publisher-and-variation]
  (if (and (some? publisher-and-variation) (str/includes? publisher-and-variation ";"))
    (subs publisher-and-variation (+ 2 (str/index-of publisher-and-variation ";")))))

(defn get-publisher
  "The given parameter should contain the variation after the semicolon, or have no variation if no semicolon is
  included in the string"
  [publisher-and-variation]
  (if (and (some? publisher-and-variation) (str/includes? publisher-and-variation ";"))
    (subs publisher-and-variation 0 (str/index-of publisher-and-variation ";"))
    publisher-and-variation))

(def almost-ready-books
  ; here, only UUID change is missing
  (->> old-books
       (map #(update % :books.book/price update-price))
       (map #(update % :books.book/book-length update-book-length))
       ; let's remove the amazon id, as it's not the ASIN!
       (map #(dissoc % :books.book/amazon-id))
       (map #(update % :books.book/item-added-date update-date))
       (map #(assoc % :books.book/item-added-date-precision "day"))
       (map #(update % :books.book/amazon-book-image-front get-filename))
       (map #(update % :books.book/amazon-thumbnail-url get-filename))
       (map #(update % :books.book/amazon-url update-asin))
       (map #(clojure.set/rename-keys % {:books.book/amazon-url :books.book/asin}))
       (map #(assoc % :books.book/publish-date (parse-date (get-release-date (:books.book/publisher %)))))
       (map #(assoc % :books.book/variation (get-book-variation (get-publisher-and-variation (:books.book/publisher %)))))
       (map #(update % :books.book/publisher (comp get-publisher-and-variation get-publisher)))))

(map (partial s/valid? :books.book/book) almost-ready-books)

(s/explain :books.book/book (first almost-ready-books))

(comment
  "Let's change the IMG files for *front covers* + *thumbnails* to use the same UUID for a given book!"

  "Let's first find them in our files..."

  (def frontcovers-path
    "mongo/src/main/resources/front_matters/")

  (def effective-frontcover-img-names
    (->> (rest (file-seq (clojure.java.io/file frontcovers-path)))
         (map str)
         (map #(subs % (+ 1 (str/last-index-of % "/"))))))

  (def frontcover-img-names
    (->> (map :books.book/amazon-book-image-front old-books)
         (filter some?)
         (map #(subs % (+ 1 (str/last-index-of % "/"))))))


  (clojure.set/difference (set frontcover-img-names)
                          (set effective-frontcover-img-names))
  ; --> They are exactly the same!

  ; Now do we also have the thumbnails somewhere?

  ;; Let's first get all thumbnail IMG names...

  (->> (map :books.book/amazon-thumbnail-url old-books)
       (map #(subs % (+ 1 (str/last-index-of % "/")))))
  ;; --> It seems that they are not saved... I cannot find them here
  ;; ==> Therefore, let's just keep the front covers for now
  ;; ==> We will need to think how we will save that from amazon in the future
  ;; ==> But this problem exists not only in regard to thumbnails, also e.g. ASIN


  (defn assoc-uuids
    "Attention! This will generate new UUIDs on every call! We will need to save them somewhere, e.g. on the disk"
    [books]
    (map #(assoc % :books.book/cover-id (str (UUID/randomUUID))) books))

  (spit
    "mongo/src/main/resources/books_template_postgres_ready.clj"
    (vec
      (->> (assoc-uuids almost-ready-books)
           (map #(update % :books.book/item-added-date str))
           (map #(update % :books.book/publish-date str)))))

  "Now, let's actually rename the images!"

  (-> frontcovers-path)

  (def ready-books
    (read-string (slurp "mongo/src/main/resources/books_template_postgres_ready.clj")))


  (->> ready-books
       (map #(let [source (str frontcovers-path (:books.book/amazon-book-image-front %))
                   target (str frontcovers-path (:books.book/cover-id %) "_front.jpg")]
               ;(println % "\n---\n" source "\n---\n" target)
               (when ((comp not nil?) (:books.book/amazon-book-image-front %))
                 (println source target)
                 (sh "git" "mv" "-f" source target)))))

  )

(def ready-books
  "Note that :books.book/amazon-book-image-front can be dismissed in here! It's the old book format"
  (read-string (slurp "mongo/src/main/resources/books_template_postgres_ready.clj")))

(defn add-single-book
  [amazon-book]
  (books-db/create-full-book!
    {:authors                (:books.book/authors amazon-book)
     :publisher              (:books.book/publisher amazon-book)

     :title                  (:books.book/title amazon-book)
     :subtitle               nil
     :added                  (LocalDate/parse (:books.book/item-added-date amazon-book))
     :asin                   (:books.book/asin amazon-book)
     :isbn-10                (:books.book/isbn-10 amazon-book)
     :isbn-13                (:books.book/isbn-13 amazon-book)
     :language               (:books.book/language amazon-book)
     :cover-image-id         (UUID/fromString (:books.book/cover-id amazon-book))
     :weight                 nil
     :price                  (:books.book/price amazon-book)
     :edition-name           (:books.book/variation amazon-book)
     :number-of-pages        (:books.book/book-length amazon-book)
     :physical-dimensions    nil
     :physical-format        nil
     :publish-country        nil
     :publish-date           (if (not (empty? (:books.book/publish-date amazon-book)))
                               (LocalDate/parse (:books.book/publish-date amazon-book)))
     :publish-date-precision "day"
     :description            (:books.book/description amazon-book)
     :notes                  nil
     :last-modified          nil}))

(comment
  ;; Let's try it for a single book!

  (count ready-books)
  (def my-book (last ready-books))

  ;; The following add the last of my books to the database
  (add-single-book my-book)

  (map :books.book/publish-date ready-books)
  (map :books.book/item-added-date ready-books)

  ;; Is date parsing working:
  (LocalDate/parse "2021-12-27"))


(comment
  ;; <<< ACTUAL MIGRATION >>>
  ;; Now, let's run the actual migration...

  ;; Run the following function to actually migrate (i.e. add) all saved books in the DB
  (do
    (println "Do you really want to add (migrate) all books into the database? Enter [y] to continue")
    (if (= "y" (read-line))
      (map add-single-book ready-books)
      (println "Migration aborted"))))
