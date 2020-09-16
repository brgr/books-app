(ns books.effects
  (:require [re-frame.core :refer [reg-fx]]
            [reitit.frontend.easy :as rfe]))

(reg-fx
  :navigate!
  (fn [route]
    (apply rfe/push-state route)))