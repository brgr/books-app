(ns books.routing
  (:require [books.views.views :as views]
            [re-frame.core :refer [dispatch]]
            [schema.core :as s]))

(def routes
  [["/"
    {:name      ::ui
     :view      views/ui
     :link-text "UI"}]
   ["/search/amazon"
    {:name        ::search-amazon
     :view        views/search-amazon-results
     :link-text   "Search Amazon"
     :controllers [{:parameters {:query [:search-text]}
                    :start      (fn [{:keys [query]}]
                                  (js/console.log "query: " (:search-text query)))}]}]
   ["/book/:id"
    {:name        ::book
     :view        views/single-book-view
     :link-text   "Book"
     :controllers [{:parameters {:path [:id]}
                    :start      (fn [{:keys [path]}]
                                  (dispatch [:update-current-book-id (:id path)])
                                  (js/console.log "start" "item controller" (:id path)))
                    :stop       (fn [{:keys [path]}]
                                  (js/console.log "stop" "item controller" (:id path)))}]}]
   ["/new"
    {:name      ::new
     :view      views/single-book-edit
     :link-text "New Book"}]
   ["/import"
    {:name      ::import
     :view      views/amazon-wishlist-forms
     :link-text "Import"}]])