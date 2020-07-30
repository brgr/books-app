(ns bookstore.import
  (:require [amazon.books.list-parser :as parser]
            [bookstore.model :as model]))

(defn import-batch [wishlist-url]
  "Blocks until all books are loaded from the wishlist. For a wishlist consisting of a few hundred elements, this
  can easily take up 10s and more. When loaded, the books are imported in the database without further check and with
  only minimal processing."
  (let [books (parser/load-books-from-amazon-wishlist-url wishlist-url)]
    (model/insert-new-books books)))