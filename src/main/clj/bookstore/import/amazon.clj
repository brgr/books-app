(ns bookstore.import.amazon
  (:require [amazon.books.parse.wishlist :refer [load-books-from-amazon-wishlist-url]]
            [amazon.books.parse.single-book :refer [load-book]]
            [amazon.books.fetch.image :refer [load-and-save-file]]
            [environ.core :refer [env]]
            [clojure.string :as str]))

(def books-loaded (atom 0))

(defn load-book-content [wishlist-book]
  (try
    (if (some? (:books.book/amazon-url wishlist-book))
      (into wishlist-book (load-book (:books.book/amazon-url wishlist-book)))
      wishlist-book)
    (catch Exception e
      (cond
        (or (str/includes? (str e) "etaoin/timeout")
            (and (str/includes? (str e) "Port")
                 (str/includes? (str e) "already in use"))) (into wishlist-book {:status :to-retry})
        :else (into wishlist-book {:status :error
                                   :error  (str e)})))))

(defn- load-full-book [wishlist-book]
  (let [book (load-book-content wishlist-book)]
    (if (nil? (:status book))
      (do
        (when-let [image-url (not-empty (:books.book/amazon-book-image-front book))]
          (load-and-save-file image-url (:front-matter-dir env)))
        (swap! books-loaded inc)
        book)
      book)))

(defn fully-load-books [wishlist-books]
  (let [loaded-books (loop [books-to-load wishlist-books
                            {:keys [successful faulty]} {:successful [], :faulty []}
                            retry-count 0]
                       (let [map-fn (if (< retry-count 3) pmap map)
                             grouped-books (->> (map-fn load-full-book books-to-load)
                                                (group-by :status))]
                         (if (or (>= retry-count 5) (empty? (:to-retry grouped-books)))
                           {:successful (apply conj successful (get grouped-books nil))
                            :faulty     (apply conj (:to-retry grouped-books) (apply conj faulty (:error grouped-books)))}
                           (do
                             (Thread/sleep (+ 10000 (rand 10000)))
                             (recur (:to-retry grouped-books)
                                    {:successful (apply conj successful (get grouped-books nil))
                                     :faulty     (apply conj faulty (:error grouped-books))}
                                    (+ retry-count 1))))))]
    (reset! books-loaded 0)
    loaded-books))

(defn import-wishlist [wishlist-url]
  (let [wishlist-books (load-books-from-amazon-wishlist-url wishlist-url)
        fully-loaded-books (fully-load-books wishlist-books)]
    fully-loaded-books))
