(ns bookstore.model-test
  (:require [clojure.test :refer :all])
  (:require [bookstore.model :as model]))

; todo: this works once, but if done multiple times the entry is created multiple times
(deftest insert-new-book-test
  (is (do
        (model/insert-new-book {:amazon-id                     "asdf"
                                :title                         "Buch Sowieso"
                                :amazon-url                    "www.amazonas.br"
                                :authors                       ["Autor 1", "Autor 2"]
                                :amazon-thumbnail-url          "www.amasdfa.sdfwe"
                                :amazon-date-added-to-wishlist "date representation!? -> Joda!"
                                :amazon-price                  {:date  "date sowieso"
                                                                :price {:amount 5.02 :currency "EUR"}}
                                ; TODO: Beschreibung, Veroeffentlichungsdatum, Verlag, user-generated tags, book language,
                                ;  original book language, length of book
                                })
        (= (count (model/all-books)) 1))))
