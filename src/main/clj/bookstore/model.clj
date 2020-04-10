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

(def host "0.0.0.0")
;(def host "165.22.76.70")
(def db-name "bookstore")

(def credentials
  (let [admin-db "admin"
        user "root"
        password (.toCharArray "GJabLafh53j4LL")]
    (credentials/create user admin-db password)))

; using default port
(defn insert-new-book [book]
  (let [connection (monger/connect-with-credentials host credentials)
        db (monger/get-db connection db-name)]
    (collection/insert-and-return db "books" book)))

(defn stringify-id [entry]
  (assoc entry :_id (str (get entry :_id))))

(defn all-books []
  (let [connection (monger/connect-with-credentials host credentials)
        db (monger/get-db connection db-name)
        mongo_entries (collection/find-maps db "books")]
    (map (partial stringify-id) mongo_entries)))
