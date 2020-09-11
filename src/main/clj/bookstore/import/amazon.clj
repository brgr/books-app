(ns bookstore.import.amazon
  (:require [amazon.books.parse.wishlist :refer [load-books-from-amazon-wishlist-url]]
            [amazon.books.parse.single-book :refer [load-book]]
            [amazon.books.fetch.image :refer [load-file-from]]))

(defn- load-full-book [book]
  (let [book (if (some? (:amazon.books/amazon-url book))
               (into book (load-book (:amazon.books/amazon-url book)))
               book)]
    ; todo: also load images
    book))

(defn import-wishlist [wishlist-url]
  "Imports into the database the whole wishlist, including the wishlist URL itself, all the book data of the wishlist
  (sourced from their respective Amazon site), as well as all the front matters of the respective books."
  (let [books (load-books-from-amazon-wishlist-url wishlist-url)]
    (pmap load-full-book books)))