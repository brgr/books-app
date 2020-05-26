(ns books.routing
  (:require [books.views.views :as views]))

(def routes
  [["/"
    {:name      ::ui
     :view      views/ui
     :link-text "UI"}]
   ["/new"
    {:name      ::new
     :view      views/single-book-edit
     :link-text "New Book"}]
   ["/import"
    {:name      ::import
     :view      views/amazon-wishlist-forms
     :link-text "Import"}]])