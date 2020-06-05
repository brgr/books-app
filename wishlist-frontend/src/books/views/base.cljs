(ns books.views.base
  (:require [re-frame.core :refer [subscribe]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:ul
    [:li [:a {:href (rfe/href :books.routing/ui)} "Homepage"]]
    [:li [:a {:href (rfe/href :books.routing/new)} "New Book"]]
    [:li [:a {:href (rfe/href :books.routing/import)} "Import"]]]])
