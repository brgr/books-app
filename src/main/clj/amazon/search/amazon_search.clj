(ns amazon.search.amazon-search
  (:require
    [amazon.search.parse.authors :refer [get-authors-from-metadata-below-title]]
    [amazon.search.parse.search-result :refer [SearchResult parse-search-results]]
    [schema.core :as s])
  (:import
    [org.jsoup Jsoup]
    (java.net URL URLEncoder)
    (org.jsoup.select Elements)
    (org.jsoup.nodes Document)))

(defn build-amazon-search-url
  [search]
  (str "https://www.amazon.de/s?k=" (URLEncoder/encode search "UTF-8")))

(s/defn get-results :- Elements
  [soup :- Document]
  (.select soup "div.s-result-item[data-component-type=s-search-result]"))

(s/defn find-results :- [SearchResult]
  [soup :- Jsoup]
  (-> (get-results soup)
      (parse-search-results)))

(defn- starts-url-slug-strangely?
  [search-result]
  (clojure.string/starts-with? (:product-url-slug search-result) "/gp/slredirect/picassoRedirect.html"))

(s/defn filter-shady-results :- [SearchResult]
  "Filters strange results out. It seems that Amazon gives these out on purpose, but I'm not sure."
  [original-search-results :- [SearchResult]]
  (filter #(not (starts-url-slug-strangely? %)) original-search-results))

(s/defn search-amazon :- [SearchResult]
  "Returns mostly all search results without really much filtering. I.e., not only books are returned, but all kind of
   products. Often times, products that are not books will have no authors."
  [search :- String]
  (let [search-url (URL. (build-amazon-search-url search))
        timeout 5000]
    (-> (find-results (Jsoup/parse search-url timeout))
        (filter-shady-results))))

(comment
  (search-amazon "Moby Dick"))