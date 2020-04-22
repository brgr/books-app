(ns books.events
  (:require [ajax.core :refer [GET POST json-request-format]]
            [re-frame.core :refer [reg-event-db dispatch]]))

(def localhost "http://localhost")
(def port "3000")
(def url (str localhost ":" port))

(reg-event-db
  :initialize-books
  (fn [_ _]
    (let [_ (GET (str url "/books")
                 {:handler       #(dispatch [:process-all-books (%1 :result)])
                  ; TODO: error handler
                  :error-handler #(do (println "error:" %1))})]
      {:single-book {:book-title "Buchtitel"}
       :all-books   []})))

(reg-event-db
  :insert-book-to-db
  (fn [db [_ new-book]]
    (do
      ; TODO: afterwards initiate a fetch to get all books anew
      (POST (str url "/books/book") {:params new-book
                                     :format :json
                                     :handler         #(println "Worked fine:" %1)
                                     :error-handler   #(println "Error:" %1)})
      db)))

(reg-event-db
  :process-all-books
  (fn [db [_ all-books]]
    (assoc db :all-books all-books)))

(reg-event-db
  :edit-single-book
  (fn [db [_ new-single-book]]
    (assoc db :single-book new-single-book)))

(reg-event-db
  :update-in-single-book
  (fn [db [_ path new-value]]
    (assoc-in db [:single-book (first path)] new-value)))
