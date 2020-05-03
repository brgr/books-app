(ns books.views.base
  (:require [reitit.frontend.easy :as routing]
            [books.views.views :as views]))

(defn current-page []
  [:div
   [:ul
    [:li [:a {:href (routing/href ::ui)} "Books UI"]]
    [:li [:a {:href (routing/href ::frontpage)} "Frontpage"]]
    [:li [:a {:href (routing/href ::about)} "About"]]
    [:li [:a {:href (routing/href ::item {:id 1})} "Item 1"]]
    [:li [:a {:href (routing/href ::item {:id 2} {:foo "bar"})} "Item 2"]]]
   (if @views/match
     (let [view (:view (:data @views/match))]
       [view @views/match]))
   [:pre (with-out-str (cljs.pprint/pprint @views/match))]])