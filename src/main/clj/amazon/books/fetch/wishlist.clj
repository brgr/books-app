(ns amazon.books.fetch.wishlist
  (:require [etaoin.api :refer :all]
            [amazon.books.fetch.scraping.driver :refer [get-driver]]))

; fixme: move this comment, together with a general explanation for  the driver to some Markdown file
; Remember to install Geckodriver on the machine that this is run on!
; https://github.com/mozilla/geckodriver

(defn- more-books-available? [driver]
  (not (empty? (query-all driver {:tag :div :fn/has-class :wl-see-more}))))

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
  (let [driver (get-driver headless?)]
    (try
      (get-source-of-whole-wishlist driver wishlist-url)
      (finally
        (quit driver)))))
