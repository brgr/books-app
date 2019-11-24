(ns bookstore.model
  (:require [monger.core :as mg]
            [monger.collection :as mc]))

; To start MongoDB:
; mongod --dbpath=./resources/bookstore


(def db_name "bookstore")

; using localhost + default port
(defn insert_new_book [book]
  (let [connection (mg/connect)
        db (mg/get-db connection db_name)]
    (mc/insert-and-return db "books" book)))


(insert_new_book {
   :amazon-id "asdf"
   :title "Buch Sowieso"
   :amazon-url "www.amazonas.br"
   :authors ["Autor 1", "Autor 2"]
   :amazon-thumbnail-url "www.amasdfa.sdfwe"
   :amazon-date-added-to-wishlist "date representation!? -> Joda!"
   :amazon-price {:date "date sowieso" :price {:amount 5.02 :currency "EUR"}}
   ; TODO: Beschreibung, Veroeffentlichungsdatum, Verlag, user-generated tags, book language,
   ;  original book language, length of book
   })