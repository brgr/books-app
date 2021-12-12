(ns bookstore.db.data.books
  (:require
    [bookstore.db.core :as core]
    [bookstore.db.queries :as queries]
    [conman.core :as conman])
  (:import (java.time LocalDateTime)))

(defn- create-author-and-get!
  [author-name]
  (let [author-row (first (queries/create-author! author-name))]
    (or author-row (queries/get-author-by-name author-name))))

(defn- create-authors-and-get!
  [author-names]
  (map create-author-and-get! author-names))

(defn create-book-authors!
  [book-id author-ids]
  (map (partial queries/create-book-author! book-id) author-ids))

(defn create-publisher-and-get!
  [publisher-name]
  (if publisher-name
    (let [publisher-row (first (queries/create-publisher! publisher-name))]
      (or publisher-row
          (queries/get-publisher-by-name publisher-name)))))

(defn create-full-book!
  [book]
  (conman/with-transaction [core/*db* {}]
    (let [author-names (:authors book)
          publisher-name (:publisher book)
          book (dissoc book :authors :publisher)
          author-rows (create-authors-and-get! author-names)
          publisher-row (create-publisher-and-get! publisher-name)
          added-book (first (queries/create-book! book (:id publisher-row)))]
      (create-book-authors! (:id added-book) (map :id author-rows)))))



(comment

  (create-full-book!
    {:title "Book4"
     :added (LocalDateTime/now)})

  ; An example to show that it works:
  (create-full-book!
    {:title     "Book3"
     :added     (LocalDateTime/now)
     :authors   ["Autor1"]
     :publisher "Publisher1"})

  ; Let's add a book with multiple authors
  (create-full-book!
    {:title     "Book3"
     :added     (LocalDateTime/now)
     :authors   ["Autor2", "Autor3"]
     :publisher "Publisher1"})

  ; Let's add a book with two authors: one that already exists, and one that is new
  (create-full-book!
    {:title     "Book3"
     :added     (LocalDateTime/now)
     :authors   ["Autor2", "Autor4"]
     :publisher "Publisher1"})

  )
