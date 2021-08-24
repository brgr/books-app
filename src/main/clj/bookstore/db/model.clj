(ns bookstore.db.model
  (:require
    [monger.collection :as collection]
    [monger.conversion :as conversion]
    [monger.query :refer [with-collection find]]
    [bookstore.db.access :refer [get-db books-collection stringify-id]])
  (:import (org.bson.types ObjectId)))

(defn insert-new-book [book]
  (let [db (get-db)]
    (-> (collection/insert-and-return db books-collection book)
        (stringify-id))))

(defn insert-new-books [books]
  (let [db (get-db)]
    (collection/insert-batch db books-collection books)))

(defn insert-new-wishlist-url [url]
  (let [db (get-db)]
    (-> (collection/insert-and-return db "wishlists" {:url url})
        (stringify-id))))

(defn all-books []
  (let [db (get-db)
        all-entries (collection/find-maps db books-collection)]
    (map stringify-id all-entries)))

(defn first-n-books [n]
  (let [db (get-db)
        first-n-books (with-collection db books-collection
                        (monger.query/find {})
                        (monger.query/limit n))]
    (map stringify-id first-n-books)))

(defn remove-book-with-amazon-id [amazon-id]
  (let [db (get-db)]
    (collection/remove db books-collection {:amazon-id amazon-id})))

(defn remove-book-by-id [id]
  (let [db (get-db)]
    (collection/remove-by-id db books-collection (ObjectId. id))))

(defn remove-all-books []
  (let [db (get-db)]
    (collection/remove db books-collection)))

(defn get-book-by-id [id]
  (let [db (get-db)
        book (-> (collection/find-by-id db books-collection (ObjectId. id))
                 (conversion/from-db-object true)
                 (stringify-id))]
    (if (empty? (book :_id)) nil book)))
