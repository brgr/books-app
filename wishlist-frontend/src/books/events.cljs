(ns books.events
  (:require [re-frame.core :refer [reg-event-db]]))

(reg-event-db
  :initialize-books
  (fn [_ _]
    {:single-book {:book-title "Buchtitel"}}))

(reg-event-db
  :edit-single-book
  (fn [db [_ new-single-book]]
    (assoc db :single-book new-single-book)))

(reg-event-db
  :update-in-single-book
  (fn [db [_ path new-value]]
    (assoc-in db [:single-book (first path)] new-value)))
