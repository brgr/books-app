(ns amazon.books.list-parser
  (:require [crouton.html :as html])
  (:import [org.jsoup Jsoup]))

; https://github.com/igrishaev/etaoin#getting-stated

;(println (System/getProperty "user.dir"))
(def wishlist_page (html/parse "../../../resources/wishlist_page.html"))
(def wishlist_html_items_file "itemNames_reddit.html")
(def wishlist_html_items (slurp wishlist_html_items_file))

(def whole_html (slurp "whole_html.html"))

;(println wishlist_page)

(defn has_correct_attrs? [ele]
  (contains? ele :attrs))

;(spit "output.clj" wishlist_page)

; The list items are in these elements: "{:tag :span, :attrs {:class "a-list-item"}, :content ..." in :content!
; Although they give a little bit too many results. They need to be filtered again.

(defn find-all-nested
  [m k]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep k)))

(def items (find-all-nested wishlist_page :tag))

;; with JSoup
; for selectors, see this reddit post:
; https://old.reddit.com/r/amazon/comments/7paenb/amazon_wishlist_web_scraper/

(def soup (Jsoup/parse whole_html))

(def wishlist_items (.select soup "li.g-item-sortable"))

(defn get_data [wishlist_item]
  (let [id (.attr wishlist_item "data-itemid")
        title (.text (.select wishlist_item (str "#itemName_" id)))
        url (.attr (.select wishlist_item (str "#itemName_" id)) "href")
        author (.text (.select wishlist_item (str "#item-byline-" id)))
        thumbnail (.attr (.select wishlist_item (str "#itemImage_" id " img")) "src")
        itemAddedDate (.text (.select wishlist_item (str "#itemAddedDate_" id)))
        price (.text (.select wishlist_item ".itemUsedAndNewPrice"))]
    {:id id :title title :author author :url url :thumbnail thumbnail :itemAddedDate itemAddedDate :price price}))

(println (mapv get_data wishlist_items))
