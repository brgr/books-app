(ns quick-actions
  (:require [bookstore.db.model :as db]))

(comment
  (db/remove-all-books))

(comment
  (def books
    (read-string (slurp "src/main/resources/books_template.clj")))
  (-> books)
  (db/insert-new-books books))