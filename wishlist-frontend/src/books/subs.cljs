(ns books.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(defn reg-sub-from-db [field]
  (reg-sub
    field
    (fn [db _]
      (field db))))

(reg-sub-from-db :single-book)
(reg-sub-from-db :all-books)
(reg-sub-from-db :current-amazon-wishlist)
