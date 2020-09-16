(ns books.views.base
  (:require [re-frame.core :refer [subscribe]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:div#title
    [:p "Books \uD83D\uDCD6"]]
   [:div [:a {:href (rfe/href :books.routing/ui)} "Homepage"]]
   [:div [:a {:href (rfe/href :books.routing/new)} "New Book"]]
   [:div [:a {:href (rfe/href :books.routing/import)} "Import"]]])
