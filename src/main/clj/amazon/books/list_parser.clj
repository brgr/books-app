(ns amazon.books.list-parser
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [amazon.books.dynamic-site-fetch :as amazon-fetch])
  (:import [org.jsoup Jsoup]))

; For selectors, see this reddit post:
; https://old.reddit.com/r/amazon/comments/7paenb/amazon_wishlist_web_scraper/

; todo: maybe do this with a real parser combinator? (like parcom in SML)
(defn get-author [amazon-author-string]
  (if (str/starts-with? amazon-author-string "von: ")
    (subs amazon-author-string 5)
    amazon-author-string))

(defn- book-data [wishlist-item]
  (let [id (.attr wishlist-item "data-itemid")
        title (->> (str "#itemName_" id) (.select wishlist-item) (.text))
        url (as-> (str "#itemName_" id) element
                  (.select wishlist-item element)
                  (.attr element "href"))
        author (->> (str "#item-byline-" id) (.select wishlist-item) (.text) (get-author))
        thumbnail (as-> (str "#itemImage_" id " img") element
                        (.select wishlist-item element)
                        (.attr element "src"))
        itemAddedDate (->> (str "#itemAddedDate_" id) (.select wishlist-item) (.text))
        price (->> (.select wishlist-item ".itemUsedAndNewPrice") (.text))]
    {:amazon-id     id
     :title         title
     :author        author
     :amazon-url    url
     :thumbnail     thumbnail
     :itemAddedDate itemAddedDate
     :price         price}))
; todo: change price and author (remove unnecessary things)

(defn load-books-from-amazon-wishlist-html [wishlist-html-content]
  (let [soup (Jsoup/parse wishlist-html-content)
        wishlist-items (.select soup "li.g-item-sortable")]
    (mapv book-data wishlist-items)))

(defn load-books-from-amazon-wishlist-url [url]
  (let [html (amazon-fetch/get-wishlist-html url true)]
    (load-books-from-amazon-wishlist-html html)))
