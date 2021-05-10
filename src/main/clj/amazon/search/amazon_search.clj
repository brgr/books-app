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

(s/defn search-amazon :- [SearchResult]
  [search :- String]
  (let [search-url (URL. (build-amazon-search-url search))
        timeout 5000]
    (find-results (Jsoup/parse search-url timeout))))

; todo: check what happens when there is no author