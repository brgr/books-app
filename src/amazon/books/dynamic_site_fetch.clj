(ns amazon.books.dynamic-site-fetch)

(use 'etaoin.api)

; Remember to install Geckodriver on the machine that this is run on!
; https://github.com/mozilla/geckodriver


(defn more_books_available? [driver]
  (.contains (get-element-inner-html driver {:id :wishlist-page}) "wl-see-more"))

(defn load_all_books [driver]
  (while (more_books_available? driver)
    (scroll-query driver {:id :navBackToTop})
    (scroll-up driver 300)))

(defn get_source_of_whole_wishlist [driver wishlist_url]
  (doto driver
    (go wishlist_url)
    (wait-visible [{:id :twotabsearchtextbox}])
    (load_all_books))
  ; get-source needs to be outside of (doto ...), otherwise it is not returned
  (get-source driver))

(defn get_wishlist_html [wishlist_url headless?]
  (let [driver (firefox {:headless headless?})]
    (try
      (get_source_of_whole_wishlist driver wishlist_url)
      (finally
        (quit driver)))))


(def wishlist_url "https://amazon.de/hz/wishlist/ls/13XXXLP6RR1X9/ref=nav_wishlist_lists_1?_encoding=UTF8&type=wishlist")

(def whole_html (get_wishlist_html wishlist_url false))
;(println whole_html)
(spit "resources/file.txt" "test file content")
(spit "whole.html" whole_html)