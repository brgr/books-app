(ns books.views.base
  (:require [re-frame.core :refer [subscribe dispatch]]
            [reitit.frontend.easy :as rfe]))

(defn nav []
  [:div {:class "navbar"}
   [:div#title
    [:a {:href (rfe/href :books.routing/ui)} "Books \uD83D\uDCD6"]]
   [:div.item [:a {:href (rfe/href :books.routing/new)} "add"]]
   [:div.item [:a {:href (rfe/href :books.routing/import)} "import"]]])

(defn text-input
  "A function that will display a generic text input field (<input />), but thereby catching 'Enter' and performing a
  given action when that is clicked.
  The keys are the normal keys, as given to hiccup :input, with two special cases:
   - :on-change - gets a function which gets passed the whole text of the input as the first parameter
   - :on-enter  - gets a function which is called when 'Enter' is pressed, without any parameters

   Note that as for the keys of the :input, :on-change and :on-key-press will get overwritten by this function, if
   they are given in the keys parameter."
  [keys]
  [:input
   (merge
     {:type         "text"
      :on-change    #((:on-change keys) (-> % .-target .-value))
      :on-key-press #(when (= (.-key %) "Enter")
                       ((:on-enter keys)))}
     (dissoc keys :on-change :on-enter :on-key-press))])

(defn search-bar []
  (let [current-search @(subscribe [:current-search])]
    [:div.search-outer-container
     [:div.search-inner-container
      (text-input
        {:class       "searchTerm"
         :on-change   (fn [text] (dispatch [:update-current-search text]))
         :on-enter    (fn [] (rfe/push-state
                               :books.routing/search-amazon
                               nil
                               {:search-text @(subscribe [:current-search])}))
         :placeholder "Search..."})
      [:a.searchButton
       {:href (rfe/href
                :books.routing/search-amazon
                nil                                         ; to be honest, I don't know what is expected here for this param!?
                {:search-text current-search})}
       "⚲"]]]))
