(ns books.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn reg-sub-from-db [field]
  (reg-sub
    field
    (fn [db _]
      (field db))))

; todo: I think it makes sense to use fully-qualified keywords here? i.e. with :: instead of :
(reg-sub-from-db :single-book)
(reg-sub-from-db :all-books)
(reg-sub-from-db :current-amazon-wishlist)
(reg-sub-from-db :current-route)
(reg-sub-from-db :current-book-id)