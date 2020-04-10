(ns bookstore.model
  (:require [monger.core :as monger]
            [monger.collection :as mc])
  ;(:require [validateur.validation :refer :all])
  )

; To start MongoDB:
; mongod --dbpath=./resources/bookstore
; or, better, with Docker:
; docker run -p 27017-27019:27017-27019 --name mongodb -d mongo

(def host "0.0.0.0")
;(def host "165.22.76.70")
(def db-name "bookstore")

; using default port
(defn insert-new-book [book]
  (let [connection (monger/connect {:host host})
        db (monger/get-db connection db-name)]
    (mc/insert-and-return db "books" book)))

(defn stringify-id [entry]
  (assoc entry :_id (str (get entry :_id))))

(def all-books
  (let [connection (monger/connect {:host host})
        db (monger/get-db connection db-name)
        mongo_entries (mc/find-maps db "books")]
    (map (partial stringify-id) mongo_entries)))


;(insert-new-book {:amazon-id                     "asdf"
;                  :title                         "Buch Sowieso"
;                  :amazon-url                    "www.amazonas.br"
;                  :authors                       ["Autor 1", "Autor 2"]
;                  :amazon-thumbnail-url          "www.amasdfa.sdfwe"
;                  :amazon-date-added-to-wishlist "date representation!? -> Joda!"
;                  :amazon-price                  {:date  "date sowieso"
;                                                  :price {:amount 5.02 :currency "EUR"}}
;                  ; TODO: Beschreibung, Veroeffentlichungsdatum, Verlag, user-generated tags, book language,
;                  ;  original book language, length of book
;                  })
