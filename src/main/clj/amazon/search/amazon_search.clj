(ns amazon.search.amazon-search
  (:require
    [amazon.search.parse.authors :refer [get-authors-from-metadata-below-title]]
    [schema.core :as s])
  (:import
    [org.jsoup Jsoup]
    (java.net URL URLEncoder)
    (org.jsoup.select Elements)
    (org.jsoup.nodes Document Element)))

(defn build-amazon-search-url
  [search]
  (str "https://www.amazon.de/s?k=" (URLEncoder/encode search "UTF-8")))

(s/defn get-results :- Elements
  [soup :- Document]
  (.select soup "div.s-result-item[data-component-type=s-search-result]"))

(s/defn get-thumbnail-url :- String
  [search-result :- Element]
  (-> (.select search-result "img")
      (first)
      (.attr "src")))

(s/defn get-product-url-slug :- String
  [search-result :- Element]
  (-> (.select search-result "h2 > a")
      (.attr "href")))

(s/defn get-title :- String
  [search-result :- Element]
  (-> (.select search-result "h2 span")
      (.text)))

(s/defn get-metadata-below-title :- String
  [search-result :- Element]
  (as-> (.select search-result "div.a-section > h2") data
        (.parents data)
        (first data)
        (.select data "div.a-color-secondary > div.a-row")
        (first data)
        (.children data)
        (map #(.text %) data)
        (clojure.string/join " " data)))

(def SearchResult
  {:title            String
   :product-url-slug String
   :thumbnail-url    String
   :authors          [String]})

(s/defn parse-search-result :- String
  [search-result :- Element]
  {:title            (get-title search-result)
   :product-url-slug (get-product-url-slug search-result)
   :thumbnail-url    (get-thumbnail-url search-result)
   :authors          (-> (get-metadata-below-title search-result)
                         (get-authors-from-metadata-below-title))})

(s/defn parse-search-results :- [SearchResult]
  [search-results :- Elements]
  (map parse-search-result search-results))

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