(ns bookstore.db.access
  (:require [monger.core :as monger]
            [monger.credentials :as credentials]
            [environ.core :refer [env]]))

(def host (env :database-url))
(def db-name "bookstore")
(def admin-db "admin")
(def user "root")
(def password (.toCharArray "GJabLafh53j4LL"))

(def books-collection "books")

(defn get-db []
  (let [credentials (credentials/create user admin-db password)
        connection (monger/connect-with-credentials host credentials)]
    (monger/get-db connection db-name)))

(defn stringify-id [entry]
  (assoc entry :_id (str (get entry :_id))))
