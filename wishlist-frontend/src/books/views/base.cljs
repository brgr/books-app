(ns books.views.base
  (:require [re-frame.core :refer [subscribe]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:div#title
    [:a {:href (rfe/href :books.routing/ui)} "Books \uD83D\uDCD6"]]
   [:div.item [:a {:href (rfe/href :books.routing/new)} "add"]]
   [:div.item [:a {:href (rfe/href :books.routing/import)} "import"]]])
