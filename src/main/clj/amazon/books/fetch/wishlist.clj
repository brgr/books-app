(ns amazon.books.fetch.wishlist
  (:require [etaoin.api :refer :all]))

; Remember to install Geckodriver on the machine that this is run on!
; https://github.com/mozilla/geckodriver

(defn- more-books-available? [driver]
  (-> (get-element-inner-html driver {:id :wishlist-page})
      (.contains "wl-see-more")))

(defn- load-all-books [driver]
  (while (more-books-available? driver)
    (scroll-query driver {:id :navBackToTop})
    (scroll-up driver 300)))

(defn- get-source-of-whole-wishlist [driver wishlist-url]
  (doto driver
    (go wishlist-url)
    (wait-visible {:id :twotabsearchtextbox})
    (load-all-books))
  ; get-source needs to be outside of (doto ...), otherwise it is not returned
  (get-source driver))

(defn get-wishlist-html [wishlist-url headless?]
  (let [driver (firefox {:headless headless?})]
    (try
      (get-source-of-whole-wishlist driver wishlist-url)
      (finally
        (quit driver)))))
