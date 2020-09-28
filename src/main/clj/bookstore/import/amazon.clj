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
  (loop [books-to-load wishlist-books
         loaded-books []
         retry-count 0]
    (let [map-fn (if (< retry-count 3) pmap map)
          grouped-books (->> (map-fn load-full-book books-to-load)
                             (group-by :status))]
      (if (or (>= retry-count 5) (empty? (:to-retry grouped-books)))
        (conj loaded-books (get grouped-books nil))
        (recur (:to-retry grouped-books)
               (conj loaded-books (get grouped-books nil))
               (+ retry-count 1))))))

(defn import-wishlist [wishlist-url]
  "Imports into the database the whole wishlist, including the wishlist URL itself, all the book data of the wishlist
  (sourced from their respective Amazon site), as well as all the front matters of the respective books."
  (let [wishlist-books (load-books-from-amazon-wishlist-url wishlist-url)
        fully-loaded-books (fully-load-books wishlist-books)]
    (reset! books-loaded 0)
    fully-loaded-books))
