(ns books.views.base
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:div#title
    [:a {:href (rfe/href :books.routing/ui)} "Books \uD83D\uDCD6"]]
   [:div.item [:a {:href (rfe/href :books.routing/new)} "add"]]
   [:div.item [:a {:href (rfe/href :books.routing/import)} "import"]]])

(defn search-bar []
  (let [current-search @(subscribe [:current-search])]
    [:div.search-outer-container
    [:div.search-inner-container
     [:input.searchTerm {:type        "text"
                         :on-change   #(dispatch [:update-current-search (-> % .-target .-value)])
                         :placeholder "Search..."}]
     [:a.searchButton {:href (rfe/href
                               :books.routing/search-amazon
                               nil
                               {:search-text current-search})}
      "⚲"]]]))
