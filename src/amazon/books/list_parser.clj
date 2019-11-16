(ns amazon.books.list-parser
  (:require [crouton.html :as html]))

; https://github.com/igrishaev/etaoin#getting-stated

;(println (System/getProperty "user.dir"))
(def wishlist_page (html/parse "../../../resources/wishlist_page.html"))

;(println wishlist_page)

(defn has_correct_attrs? [ele]
  (contains? ele :attrs))

;(spit "output.clj" wishlist_page)

; The list items are in these elements: "{:tag :span, :attrs {:class "a-list-item"}, :content ..." in :content!
; Altough they give a little bit too many results. They need to be filtered again.

(defn find-all-nested
  [m k]
  (->> (tree-seq map? vals m)
       (filter map?)
       (keep k)))

(def items (find-all-nested wishlist_page :tag))

(println items)