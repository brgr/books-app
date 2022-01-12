(ns books.views.views
  (:require [reagent.core :as reagent]
            [re-frame.core :refer [subscribe dispatch]]
            [reitit.frontend.easy :as routing]
            [spec-tools.data-spec :as ds]))

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
     [:h2 "Add a new book"]
     [single-book-edit-input-field single-book "Titel" :title]
     [single-book-edit-input-field single-book "Autor(en)" :authors]
     [:input {:type     "button"
              :value    "Submit"
              :on-click #(dispatch [:insert-book-to-db single-book])}]]))

(defn single-book-view []
  (let [current-book-id @(subscribe [:current-book-id])
        all-books @(subscribe [:all-books])
        book (first ((group-by :_id all-books) current-book-id))]
    [:div.single-book-view
     [:h1 (book :title)]
     [:h2 (book :author)]
     [:p
      [:a {:href (str "https://amazon.de" (book :amazon-url))}
       "Amazon Link"]]
     [:img {:src (str "http://localhost:3000/books/" (book :_id) "/front_matter")
            :alt (str (book :_id))}]
     [:br]
     [:code "" (str (with-out-str (cljs.pprint/pprint book)))]]))

(defn list-all-books []
  (let [books @(subscribe [:all-books])]
    (println "books:" books)
    [:div.book-list
     ;[:p (str "Count books: " (count books))]
     [:div.book-list-grid
      (for [book books]
        ; todo: add the id to the route! (look up reitit documentation for that)
        (let [image-source (str "http://localhost:3000/book/" (book :book_id) "/front-matter")
              image-alt-text (str (book :book_id))]
          ; fixme: somehow display books that have no cover
          (when (not-empty (:cover_image_id book))
            [:div.single-book-in-list
             [:a {:href (routing/href :books.routing/book {:id (book :book_id)})}
              [:img {:src image-source, :alt image-alt-text}]
              [:br]
              ;[:p (book :title) "  "
              ; [:input {:type     "button"
              ;          :value    "x"
              ;          :on-click #(dispatch [:remove-book-by-id book])}]]
              ]])))]]))

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
     [:h2 "Import Amazon Wishlist"]
     [:p {:style {:color "red"}} "Note: This feature is not yet implemented in the frontend!"]
     [:h3 "Watched Wishlists"]
     [:p "No wishlists yet"]
     [add-new-amazon-wishlist-form]]))

(defn search-amazon-results []
  (let [search-results @(subscribe [:books-search-results])]
    (println "books search results:" search-results)
    (js/console.log search-results)
    (cljs.pprint/pprint search-results)
    [:div.search-amazon
     [:div.book-list
      ;[:p (str "Count books: " (count books))]
      [:div.book-list-grid
       (for [book search-results]
         #_[:p (:title book)]
         (let [image-source (:thumbnail-url book)]
           ; fixme: somehow display books that have no cover
           [:div.single-book-in-list
            [:a {:href (routing/href :books.routing/book {:id (book :_id)})}
             [:img {:src image-source}]
             [:br]]]))]]]))

(defn ui []
  [:div.books-ui
   [list-all-books]])
