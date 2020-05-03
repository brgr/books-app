(ns books.routing
  (:require [books.views.views :refer :all]
            [spec-tools.data-spec :as ds]))

(def routes
  [["/" {:name ::ui, :view ui}]

   ["/frontpage"
    {:name ::frontpage
     :view home-page}]

   ["/about"
    {:name ::about
     :view about-page}]

   ["/item/:id"
    {:name       ::item
     :view       item-page
     :parameters {:path  {:id int?}
                  :query {(ds/opt :foo) keyword?}}}]])