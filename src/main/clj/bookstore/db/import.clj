(ns bookstore.db.import
  (:require [amazon.books.parse.wishlist :as wishlist]
            [bookstore.db.model :as model]))

#_(defn import-batch [wishlist-url]
  "Blocks until all books are loaded from the wishlist. For a wishlist consisting of a few hundred elements, this
  can easily take up 10s and more. When loaded, the books are imported in the database without further check and with
  only minimal processing."
  (let [books (wishlist/load-books-from-amazon-wishlist-url wishlist-url)]
    (model/insert-new-books books)))