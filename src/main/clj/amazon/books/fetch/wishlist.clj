(ns amazon.books.fetch.wishlist
  (:require [etaoin.api :refer :all]
            [amazon.books.fetch.scraping.driver :refer [get-driver]]))

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

(defn get-wishlist-html [wishlist-url]
  (let [driver (get-driver)]
    (try
      (get-source-of-whole-wishlist driver wishlist-url)
      (finally
        (quit driver)))))
