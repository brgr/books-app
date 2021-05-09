(ns amazon.search.amazon-search
  (:import [org.jsoup Jsoup]
           (java.net URL)))

(def url "https://www.amazon.de/s?k=the+beauty+of+everyday+things")

(def html (Jsoup/parse (URL. url) 5000))

(def first-item (-> (.select html "div.s-result-item[data-component-type=s-search-result]")
                    (first)))

; get the thumbnail
(->> (.select first-item "img")
     (map #(.attr % "src")))

; get the URL to the product
(-> (.select first-item "h2 > a")
    (.attr "href"))

; get title
(-> (.select first-item "h2 span")
    (.text))

; with the next 2: get authors
(def metadata-below-title (-> (.select first-item "div.a-section > h2")
                              (.parents)
                              (first)
                              (.select "div.a-color-secondary > div > span")))

(as-> (map #(.text %) metadata-below-title) data
      (clojure.string/join " " data)
      (clojure.string/split data #"\|")
      (map clojure.string/trim data)
      (filter #(clojure.string/starts-with? % "von ") data)
      (first data)
      (subs data 4)
      (clojure.string/split data #" und "))