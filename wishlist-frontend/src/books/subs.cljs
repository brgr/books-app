(ns books.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
  :single-book
  (fn [db _]
    (:single-book db)))