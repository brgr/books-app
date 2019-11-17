(ns amazon-wishlist.wishlist-scraper)

(require
  '[clojure.string :as string]
  '[crouton.html :as html])

(use 'etaoin.api)
(require '[etaoin.keys :as k])
(require '[crouton.html :as html])


; Dynamically load amazon books page
; Also see:
; https://github.com/tmarble/webica/blob/master/examples/lmgtfy
; https://towardsdatascience.com/data-science-skills-web-scraping-javascript-using-python-97a29738353f

(def driver (firefox {:headless true}))
(go driver "https://amazon.de/hz/wishlist/ls/13XXXLP6RR1X9/ref=nav_wishlist_lists_1?_encoding=UTF8&type=wishlist")

(wait-visible driver [{:id :twotabsearchtextbox}])

;(println (get-element-inner-html driver {:id :wishlist-page}))
;(wait-exists driver {:class :wl-see-more})
;(println "contains? " (.contains (get-element-inner-html driver {:id :wishlist-page}) "wl-see-more")) ; this works!

(defn more_books_available? [driver]
  (.contains (get-element-inner-html driver {:id :wishlist-page}) "wl-see-more"))
; falls das nicht geht:
; es gibt auch eine "showMoreUrl" -> STRG+F in HTML file! -> dann URL laden -> nächste Entries

;(scroll-down driver 2000)
;(scroll-query driver {:tag :div :class :wl-see-more-spinner})
;(wait-invisible driver {:class :wl-see-more-spinner})
;(scroll-query driver {:class :wl-see-more-spinner})

(defn load_all_books [driver]
  (while (more_books_available? driver)
    (scroll-query driver {:id :navBackToTop})
    (scroll-up driver 300)))

(load_all_books driver)