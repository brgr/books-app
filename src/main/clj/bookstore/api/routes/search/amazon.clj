(ns bookstore.api.routes.search.amazon
  (:require
    [schema.core :as s]
    [amazon.search.parse.search-result :refer [SearchResult]]
    [amazon.search.amazon-search :refer [search-amazon]]))

(def search-amazon-description
  "Calls https://www.amazon.de/s?k={search-text} and parses the results. Also filters some of the results, if they
  highly don't appear to be related to the results. It does however not filter only for books, so all kind of products
  are returned.")

(def amazon-search-routes
  ["/search/amazon"
   {:swagger {:tags ["search"]}}

   ["/"
    {:get {:summary    "Search products on amazon.de"
           :swagger    {:description search-amazon-description}
           :parameters {:query {:search-text s/Str}}
           :responses  {200 {:body {:result [SearchResult]}}}
           :handler    (fn [{{{:keys [search-text]} :query} :parameters}]
                         {:status 200
                          :body   {:result (search-amazon search-text)}})}}]])
