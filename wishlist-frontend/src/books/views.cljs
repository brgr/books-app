(ns books.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]))

(defn single-book-edit-input-field [single-book field-name field]
  [:div {:id (str "div." (name field) "-edit")}
   (str field-name ": ")
   [:input {:type      "text"
            :value     (single-book field)
            :on-change #(dispatch [:update-in-single-book [field] (-> % .-target .-value)])
            }]
   [:br] [:br]])

(defn single-book-edit []
  (let [single-book @(subscribe [:single-book])]
    [:div.single-book-edit
     [single-book-edit-input-field single-book "Titel" :book-title]
     [single-book-edit-input-field single-book "Autor(en)" :authors]
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
   [:h1 "Books"]
   [:h2 "All Books"]
   [list-all-books]
   [:hr]
   [:h2 "Add a new book"]
   [single-book-edit]])