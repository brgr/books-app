(ns bookstore.model
  (:require [monger.core :as monger]
            [monger.credentials :as credentials]
            [monger.collection :as collection]
            [monger.conversion :as conversion]
            [environ.core :refer [env]])
  ;(:require [validateur.validation :refer :all])
  (:import org.bson.types.ObjectId))

; always using default port
(def host (env :database-url))
;(def host "165.22.76.70")
(def db-name "bookstore")
(def admin-db "admin")
(def user "root")
(def password (.toCharArray "GJabLafh53j4LL"))

(def collection "books")

(defn stringify-id [entry]
  (assoc entry :_id (str (get entry :_id))))

(defn get-db []
  (let [credentials (credentials/create user admin-db password)
        connection (monger/connect-with-credentials host credentials)]
    (monger/get-db connection db-name)))

(defn insert-new-book [book]
  (let [db (get-db)]
    (-> (collection/insert-and-return db collection book)
        (stringify-id))))

(defn all-books []
  (let [db (get-db)
        all-entries (collection/find-maps db collection)]
    (map stringify-id all-entries)))

(defn remove-book-with-amazon-id [amazon-id]
  (let [db (get-db)]
    (collection/remove db collection {:amazon-id amazon-id})))

(defn remove-book-by-id [id]
  (let [db (get-db)]
    (collection/remove-by-id db collection (ObjectId. id))))

(defn get-book-by-id [id]
  (let [db (get-db)
        book (-> (collection/find-by-id db collection (ObjectId. id))
                 (conversion/from-db-object true)
                 (stringify-id))]
    (if (empty? (book :_id)) nil book)))
