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

; using localhost + default port
(defn insert-new-book [book]
  (let [connection (monger/connect {:host host})
        db (monger/get-db connection db-name)]
    (mc/insert-and-return db "books" book)))

(defn stringify-id [entry]
  (assoc entry :_id (str (get entry :_id))))

(def all-books
  (let [connection (monger/connect)
        db (monger/get-db connection db-name)
        mongo_entries (mc/find-maps db "books")]
    (map (partial stringify-id) mongo_entries)))


(insert-new-book {:amazon-id                     "asdf"
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
;
;(->> (amazon.books.list-parser/load-book-data-from-wishlist-html (slurp "resources/whole.html"))
;     (map insert-new-book))
;
;(defn get-file-name [uri]
;  (let [last-slash (str/last-index-of uri "/")]
;    (subs uri (+ 1 last-slash))))
;
;(get-file-name "hello/tet/at")
;
;(defn copy-file [uri]
;  (with-open [in (io/input-stream uri)
;              out (io/output-stream (str "resources/book_images/" (get-file-name uri)))]
;    (io/copy in out)))
;
;(as-> all-books books
;      (map :thumbnail books)
;      (filter some? books)
;      ;(map copy-file)
;      (nthrest books 228))
;; Note: It somehow stopped at the 230th or so to download... Idk why
;; todo: Get via the files all that are downloaded already, check
;;  them with the books and download the rest!
;;  Probably find out why it didn't download them all.
;
;; use this:
;(file-seq (io/file "resources/book_images"))
;
;; todo: create a lighter wishlist, for testing purposes