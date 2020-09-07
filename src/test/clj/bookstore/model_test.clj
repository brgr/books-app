(ns bookstore.model-test
  (:require [clojure.test :refer :all]
            [bookstore.db.model :as model]))

;(use-fixtures)

; Fixme: this works once, but if done multiple times the entry is created multiple times
(deftest ^:integration insert-and-remove-new-book-by-amazon-id
  (model/insert-new-book {:amazon-id                     "amazon-id"
                          :title                         "Buch Sowieso"
                          :amazon-url                    "actually this is no URL"
                          :authors                       ["Autor 1", "Autor 2"]
                          :amazon-thumbnail-url
                                                         "empty :/"
                          :amazon-date-added-to-wishlist "no date given"
                          :amazon-price                  {:date  "date sowieso"
                                                          :price {:amount 5.02 :currency "EUR"}}
                          ; TODO: Beschreibung, Veroeffentlichungsdatum, Verlag, user-generated tags, book language,
                          ;  original book language, length of book
                          })
  (is (= 1 (count (model/all-books))))
  (model/remove-book-with-amazon-id "amazon-id")
  (is (= 0 (count (model/all-books)))))

(deftest ^:integration remove-book-by-id-test
  (let [title "Sowieso"
        inserted-book (model/insert-new-book {:title title})
        id (inserted-book :_id)
        found-book (model/get-book-by-id id)]
    (is (= title (found-book :title)))
    (model/remove-book-by-id id)
    (is (nil? (model/get-book-by-id id)))))
