(ns bookstore.db.update
  (:require [bookstore.config :refer [env]]
            [amazon.books.fetch.image :as image-fetch]
            [bookstore.db.model :as model]
            [bookstore.db.access :refer [get-db books-collection stringify-id]]
            [monger.collection :as collection]
            [monger.operators :refer :all]
            [clojure.string :as str])
  (:import (org.bson.types ObjectId)))

#_#_#_(defn- update-book-thumbnail [book-id thumbnail-filename]
  (let [db (get-db)]
    (collection/update
      db
      books-collection
      {:_id (ObjectId. book-id)}
      {$set {:thumbnail thumbnail-filename}})))

(defn load-book-thumbnail [book-id]
  "Updates the thumbnail of a book that is already in the database and has an amazon thumbnail URL associated to it.
  What this does is take this thumbnail URL, load the image, and save it in the thumbnail directory."
  (let [thumbnail-url (:amazon-thumbnail-url (model/get-book-by-id book-id))
        thumbnail-filename (subs thumbnail-url (+ 1 (str/last-index-of thumbnail-url "/")))]
    (when (not (empty? thumbnail-url))
      (image-fetch/load-and-save-file thumbnail-url (env :thumbnails-dir))
      (-> (update-book-thumbnail book-id thumbnail-filename)
          .getN))))

(defn update-book-from-amazon-product-page [book-id new-book-data]
  "Updates the given book with the given book data, which is expected to be from Amazon's product page. Therefore, no
  information that already exists is updated, instead only new information is added."
  (let [updated-book (as-> (model/get-book-by-id book-id) book
                           (merge new-book-data book)
                           (assoc book :_id (ObjectId. book-id)))]
    (collection/update-by-id
      (get-db)
      books-collection
      (ObjectId. book-id)
      updated-book)
    (-> updated-book
        (stringify-id))))