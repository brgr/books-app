(ns bookstore.model
  (:require [monger.core :as monger]
            [monger.credentials :as credentials]
            [monger.collection :as collection])
  ;(:require [validateur.validation :refer :all])
  )

; To start MongoDB:
; mongod --dbpath=./resources/bookstore
; or, better, with Docker:
; docker run -p 27017-27019:27017-27019 --name mongodb -d mongo

; always using default port
;(def host "0.0.0.0")
;(def host "165.22.76.70")
; Note: database is the service name, which forwards to the IP address of its docker container
(def host "database")
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
