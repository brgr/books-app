(ns amazon.search.amazon-search
  (:require
    [amazon.search.parse.authors :refer [get-authors-from-metadata-below-title]]
    [schema.core :as s])
  (:import
    [org.jsoup Jsoup]
    (java.net URL)
    (org.jsoup.select Elements)
    (org.jsoup.nodes Document Element)))

(def url "https://www.amazon.de/s?k=the+beauty+of+everyday+things")

(def html (Jsoup/parse (URL. url) 5000))

(s/defn get-results :- Elements
  [soup :- Document]
  (.select soup "div.s-result-item[data-component-type=s-search-result]"))

(get-results html)

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

(s/defn parse-search-result
  [search-result :- Element]
  {:title            (get-title search-result)
   :product-url-slug (get-product-url-slug search-result)
   :thumbnail-url    (get-thumbnail-url search-result)
   :authors          (-> (get-metadata-below-title search-result)
                         (get-authors-from-metadata-below-title))})

(s/defn get-search-results
  [search-results :- Elements]
  (map parse-search-result search-results))

(get-search-results (get-results html))
; todo: It seems to me that these results are fine. Make it work for all searches + check e.g. search "book"
; todo: also check what happens when there is no author