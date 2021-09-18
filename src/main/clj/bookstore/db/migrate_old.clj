(ns bookstore.db.migrate-old
  "Used simply to migrate the old books that we had in MongoDB. Will be deleted afterwards..."
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s])
  (:import (java.time LocalDate)
           (java.time.format DateTimeFormatter)
           (java.util Locale)))

(s/def :books.book/title string?)
(s/def :books.book/language (s/nilable string?))
; todo: change key name of book img to use UUID; change all images, including thumbnails to use UUID!
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

(def ready-books
  ; todo: currently, only UUID change is missing
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

(map (partial s/valid? :books.book/book) ready-books)

(s/explain :books.book/book (first ready-books))

(comment
  "Let's change the IMG files for *front covers* + *thumbnails* to use the same UUID for a given book!"

  "Let's first find them in our files..."

  

  )

