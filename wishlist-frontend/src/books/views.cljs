(ns books.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn single-book-edit-input-field [single-book]
  [:div.title-edit
   "Titel: "
   [:input {:type      "text"
            :value     (single-book :book-title)
            ; TODO: is this the reframe way?
            :on-change #(dispatch [:update-in-single-book [:book-title] (-> % .-target .-value)])
            }]
   [:br] [:br]])

(defn single-book-edit []
  (let [single-book @(subscribe [:single-book])]
    [:div.single-book-edit
     [single-book-edit-input-field single-book]
     [:input {:type     "button"
              :value    "Submit"
              :on-click #(dispatch [:insert-book-to-db single-book])}]]))

(defn list-all-books []
  (let [books @(subscribe [:all-books])]
    [:div.book-list
     [:p (str "Count books: " (count books))]
     (for [book books]
       [:div.single-book-in-list
        [:p "Buch: " book "  "
         [:input {:type  "button"
                  :value "x"
                  :on-click #(dispatch [:remove-book-by-id book])}]]])]))

(defn ui []
  [:div.books-ui
   [list-all-books]
   [:hr]
   [single-book-edit]])