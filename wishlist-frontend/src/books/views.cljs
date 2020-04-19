(ns books.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn single-book-edit []
  (let [single-book @(subscribe [:single-book])]
    [:div.single-book-edit
     [:div.title-edit
      "Titel: "
      [:input {:type      "text"
               :value     (single-book :book-title)
               ; TODO: is this the reframe way?
               :on-change #(dispatch [:update-in-single-book [:book-title] (-> % .-target .-value)])
               }]
      [:br] [:br]
      [:input {:type     "button"
               :value    "Submit"
               :on-click (fn [_] (println "book:" @(subscribe [:single-book])))}]]]))

(defn list-all-books []
  (let [books @(subscribe [:all-books])]
    [:div.book-list
     [:p (str "Count books: " (count books))]
     (for [book books]
       [:p "Buch: " book])]))

(defn ui []
  [:div.books-ui
   [list-all-books]
   [:hr]
   [single-book-edit]])