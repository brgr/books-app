(ns quick-actions
  (:require [bookstore.db.model :as db]))

(comment
  (db/remove-all-books))

(comment
  (def books
    (read-string (slurp "mongo/resources/books_template.clj")))
  (-> books)
  (db/insert-new-books books))

(comment

  (def faulty-books
    (read-string (slurp "mongo/resources/books_from_wishlist_faulty.clj")))

  (count faulty-books)
  (def books-to-retry
    (->> faulty-books
         (group-by :status)
         :to-retry))

  ; todo: load the images for these books, then remove the error in them and use them for working on the frontend
  (def faulty-books-that-actually-worked
    (->> faulty-books
         (group-by :status)
         :error
         (filter #(contains? % :books.book/amazon-book-image-front))))

  (def really-faulty-books
    (->> faulty-books
         (group-by :status)
         :error
         (filter #(not (contains? % :books.book/amazon-book-image-front)))))

  ; fixme: the book I am currently reading (Das Versprechen) is not loaded...!
  (->> really-faulty-books
       (map :books.book/title)
       (filter #(clojure.string/includes? % "Versprechen")))
  )