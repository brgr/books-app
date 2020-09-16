(ns bookstore.import.amazon
  (:require [amazon.books.parse.wishlist :refer [load-books-from-amazon-wishlist-url]]
            [amazon.books.parse.single-book :refer [load-book]]
            [amazon.books.fetch.image :refer [load-and-save-file]]
            [environ.core :refer [env]]))

(def books-loaded (atom 0))

(defn- load-full-book [book]
  (println (str "Fetching book " (:books.book/title book) "..."))
  (let [book (try (if (some? (:books.book/amazon-url book))
                    (into book (load-book (:books.book/amazon-url book)))
                    book)
                  (catch Exception e
                    ; fixme: Error is currently thrown too much...
                    (println (str "Error for book: " (:books.book/title book) "\n" e))
                    book))]
    (when-let [image-url (not-empty (:books.book/amazon-book-image-front book))]
      (load-and-save-file image-url (:front-matter-dir env)))
    (swap! books-loaded inc)
    book))

(defn import-wishlist [wishlist-url]
  "Imports into the database the whole wishlist, including the wishlist URL itself, all the book data of the wishlist
  (sourced from their respective Amazon site), as well as all the front matters of the respective books."
  (let [books (load-books-from-amazon-wishlist-url wishlist-url)
        fully-loaded-books (pmap load-full-book books)]
    (reset! books-loaded 0)
    fully-loaded-books))