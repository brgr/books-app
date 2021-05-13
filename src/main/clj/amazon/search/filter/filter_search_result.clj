(ns amazon.search.filter.filter-search-result
  (:require
    [amazon.search.parse.search-result :refer [SearchResult]]
    [schema.core :as s]))

(defn- starts-url-slug-strangely?
  "This url slug sometimes appears in the HTML document, but the respective results never appear visibly when
  performing the search on amazon. Furthermore, when looking at them more deeply, they are almost always unrelated to
  the actual search, but seem to have to do with the user performing the search. Maybe they are advertisement?"
  [search-result]
  (clojure.string/starts-with? (:product-url-slug search-result) "/gp/slredirect/picassoRedirect.html"))

(s/defn filter-shady-results :- [SearchResult]
  "Filters strange results out. It seems that Amazon gives these out on purpose, but I'm not sure."
  [original-search-results :- [SearchResult]]
  (filter #(not (starts-url-slug-strangely? %)) original-search-results))
