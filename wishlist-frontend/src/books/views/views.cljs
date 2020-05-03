(ns books.views.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]
            [reitit.frontend.easy :as routing]))

(defn single-edit-input-field [{:keys [operated-on-object field field-text]} dispatch-id]
  [:div {:id (str "div." (name field) "-edit")}
   (str field-text ": ")
   [:input {:type      "text"
            :value     (if (nil? operated-on-object) "" (operated-on-object field))
            :on-change #(dispatch [dispatch-id [field] (-> % .-target .-value)])}]
   [:br] [:br]])

(defn single-book-edit-input-field [single-book field-name field]
  (single-edit-input-field {:operated-on-object single-book
                            :field              field
                            :field-text         field-name} :update-in-single-book))

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
         [:input {:type     "button"
                  :value    "x"
                  :on-click #(dispatch [:remove-book-by-id book])}]]])]))

(defn add-new-amazon-wishlist-form []
  (let [amazon-wishlist @(subscribe [:current-amazon-wishlist])]
    [:div.amazon-wishlist-add
     [single-edit-input-field {:operated-on-object amazon-wishlist
                               :field              :url
                               :field-text         "Wishlist URL"} :update-in-amazon-wishlist]
     [:input {:type     "button"
              :value    "Submit"
              :on-click #(dispatch [:add-new-wishlist-url (amazon-wishlist :url)])}]]))

(defn amazon-wishlist-forms []
  (let [watched-wishlists []]                               ; todo: get from backend
    [:div.amazon-wishlists
     [:h3 "Watched Wishlists"]
     [:p "No wishlists yet"]
     [add-new-amazon-wishlist-form]]
    ))

(defn ui []
  [:div.books-ui
   [:navbar "Seite 1" "Seite 2"]
   [:h1 "Books \uD83D\uDCD6"]
   [:h2 "All Books"]
   [list-all-books]
   [:hr]
   [:h2 "Add a new book"]
   [single-book-edit]
   [:hr]
   [:h2 "Import Amazon Wishlist"]
   [amazon-wishlist-forms]])

(defn home-page []
  [:div
   [:h2 "Welcome to frontend"]

   [:button
    {:type     "button"
     :on-click #(routing/push-state ::item {:id 3})}
    "Item 3"]

   [:button
    {:type     "button"
     :on-click #(routing/replace-state ::item {:id 4})}
    "Replace State Item 4"]])

(defn about-page []
  [:div
   [:h2 "About frontend"]
   [:ul
    [:li [:a {:href "http://google.com"} "external link"]]
    [:li [:a {:href (routing/href ::foobar)} "Missing route"]]
    [:li [:a {:href (routing/href ::item)} "Missing route params"]]]

   [:div
    {:content-editable               true
     :suppressContentEditableWarning true}
    [:p "Link inside contentEditable element is ignored."]
    [:a {:href (routing/href ::frontpage)} "Link"]]])

(defn item-page [match]
  (let [{:keys [path query]} (:parameters match)
        {:keys [id]} path]
    [:div
     [:h2 "Selected item " id]
     (if (:foo query)
       [:p "Optional foo query param: " (:foo query)])]))

; todo: use reframe for this!
(defonce match (reagent.core/atom nil))

