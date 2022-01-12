(ns amazon.books.parse.wishlist
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [amazon.books.fetch.wishlist :as wishlist])
  (:import [org.jsoup Jsoup]))

; For selectors, see this reddit post:
; https://old.reddit.com/r/amazon/comments/7paenb/amazon_wishlist_web_scraper/

(defn- get-author [amazon-author-string]
  (if (str/starts-with? amazon-author-string "von: ")
    (subs amazon-author-string 5)
    amazon-author-string))

(defn- book-data [wishlist-item]
  ; FIXME: This id here might be the item ID, but *NOT* the amazon ID! With amazon ID, we would understand the ASIN,
  ;  which can also be found from this site, but it's not the same as this. Change it!
  (let [id (.attr wishlist-item "data-itemid")
        title (->> (str "#itemName_" id)
                   (.select wishlist-item)
                   (.text))
        url (as-> (str "#itemName_" id) element
                  (.select wishlist-item element)
                  (.attr element "href")
                  ; url is empty when there is a dead element on the wishlist (a book that was later removed from
                  ; Amazon)
                  (if (empty? element)
                    nil
                    element))
        author (->> (str "#item-byline-" id)
                    (.select wishlist-item)
                    (.text)
                    (get-author))
        thumbnail (as-> (str "#itemImage_" id " img") element
                        (.select wishlist-item element)
                        (.attr element "src"))
        item-added-date (->> (str "#itemAddedDate_" id)
                             (.select wishlist-item)
                             (.text))
        price (->> (.select wishlist-item ".itemUsedAndNewPrice")
                   (.text))]
    {:books.book/amazon-id            id
     :books.book/title                title
     ;:author               author
     :books.book/amazon-url           url
     :books.book/amazon-thumbnail-url thumbnail
     :books.book/item-added-date      item-added-date
     :books.book/price                price}))

(defn load-books-from-amazon-wishlist-html [wishlist-html-content]
  (let [soup (Jsoup/parse wishlist-html-content)
        wishlist-items (.select soup "li.g-item-sortable")]
    (mapv book-data wishlist-items)))

(defn load-books-from-amazon-wishlist-url [url]
  (let [html (wishlist/get-wishlist-html url)]
    (load-books-from-amazon-wishlist-html html)))
