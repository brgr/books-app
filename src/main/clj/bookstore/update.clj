(ns bookstore.update
  (:require [environ.core :refer [env]]
            [amazon.books.image-fetch :as image-fetch]
            [bookstore.model :as model]
            [bookstore.access :refer [get-db books-collection stringify-id]]
            [monger.collection :as collection]
            [monger.operators :refer :all]
            [clojure.string :as str])
  (:import (org.bson.types ObjectId)))

(defn update-book-thumbnail [book-id thumbnail-filename]
  (let [db (get-db)]
    (collection/update db
                       books-collection
                       {:_id (ObjectId. book-id)}
                       {$set {:thumbnail thumbnail-filename}})))

(defn load-book-thumbnail [book-id]
  "Updates the thumbnail of a book that is already in the database and has an amazon thumbnail URL associated to it.
  What this does is take this thumbnail URL, load the image, and save it in the thumbnail directory."
  (let [thumbnail-url (:amazon-thumbnail-url (model/get-book-by-id book-id))
        thumbnail-filename (subs thumbnail-url (+ 1 (str/last-index-of thumbnail-url "/")))]
    (when (not (empty? thumbnail-url))
      (image-fetch/load-file-from thumbnail-url (env :thumbnails-dir))
      (-> (update-book-thumbnail book-id thumbnail-filename)
          .getN))))
