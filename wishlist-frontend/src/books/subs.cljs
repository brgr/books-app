(ns books.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
  :single-book
  (fn [db _]
    (:single-book db)))

(reg-sub
  :all-books
  (fn [db _]
    (:all-books db)))