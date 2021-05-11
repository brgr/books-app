(ns amazon.search.parse.search-result
  (:require
    [amazon.search.parse.authors :refer [get-authors-from-metadata-below-title]]
    [schema.core :as s])
  (:import
    (org.jsoup.select Elements)
    (org.jsoup.nodes Element)))

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

(defn- contains-whole-title-also-subtitles?
  [whole-title-container]
  (= 1 (.children whole-title-container)))

(s/defn get-metadata-below-title :- String
  [search-result :- Element]
  (let [whole-title-container (as-> (.select search-result "div.a-section > h2") data
                                    (.parents data)
                                    (first data))]
    (if (contains-whole-title-also-subtitles? whole-title-container)
      (as-> (.select whole-title-container "div.a-color-secondary > div.a-row") data
            (first data)
            (.children data)
            (map #(.text %) data)
            (clojure.string/join " " data)))))

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
