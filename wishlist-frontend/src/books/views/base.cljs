(ns books.views.base
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:div#title
    [:a {:href (rfe/href :books.routing/ui)} "Books \uD83D\uDCD6"]]
   [:div.item [:a {:href (rfe/href :books.routing/new)} "add"]]
   [:div.item [:a {:href (rfe/href :books.routing/import)} "import"]]])

(defn text-input [keys on-change-trigger on-enter-trigger on-enter-sub]
  [:input
   (merge
     {:type         "text"
      :on-change    (fn [e]
                      (dispatch [on-change-trigger (-> e .-target .-value)]))
      :on-key-press (fn [e]
                      (when (= (.-key e) "Enter")
                        (on-enter-trigger @(subscribe [on-enter-sub]))))}
     (dissoc keys :on-change :on-enter :on-key-press))])

(defn search-bar []
  (let [current-search @(subscribe [:current-search])]
    [:div.search-outer-container
     [:div.search-inner-container
      (text-input
        {:class             "searchTerm"
         ;:on-enter          #(do
         ;                      (println "Current search: " %1)
         ;                      (rfe/push-state
         ;                        :books.routing/search-amazon
         ;                        nil
         ;                        {:search-text %1}))
         :on-change-trigger :update-current-search
         ;:on-change   #(dispatch [:update-current-search (-> % .-target .-value)])
         :placeholder       "Search..."}
        :update-current-search
        #(do
           (println "Current search: " %1)
           (rfe/push-state
             :books.routing/search-amazon
             nil
             {:search-text %1}))
        :current-search)
      [:a.searchButton
       {:href (rfe/href
                :books.routing/search-amazon
                nil                                         ; to be honest, I don't know what is expected here for this param!?
                {:search-text current-search})}
       "⚲"]]]))
