(ns amazon.books.list-parser
  (:import [org.jsoup Jsoup]))


; For selectors, see this reddit post:
; https://old.reddit.com/r/amazon/comments/7paenb/amazon_wishlist_web_scraper/

(defn get_data [wishlist_item]
  (let [id (.attr wishlist_item "data-itemid")
        title (.text (.select wishlist_item (str "#itemName_" id)))
        url (.attr (.select wishlist_item (str "#itemName_" id)) "href")
        author (.text (.select wishlist_item (str "#item-byline-" id)))
        thumbnail (.attr (.select wishlist_item (str "#itemImage_" id " img")) "src")
        itemAddedDate (.text (.select wishlist_item (str "#itemAddedDate_" id)))
        price (.text (.select wishlist_item ".itemUsedAndNewPrice"))]
    {:id id :title title :author author :url url :thumbnail thumbnail :itemAddedDate itemAddedDate :price price}))

(defn load_book_data_from_wishlist [whole_wishlist_html]
  (let [soup (Jsoup/parse whole_html)
        wishlist_items (.select soup "li.g-item-sortable")]
    (mapv get_data wishlist_items)))


(println (load_book_data_from_wishlist (slurp "whole_html.html")))
