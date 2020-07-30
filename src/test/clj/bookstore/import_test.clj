(ns bookstore.import-test
  (:require [clojure.test :refer :all])
  (:require [bookstore.import :refer [import-batch]]))

(def test-wishlist-local "src/test/resources/test-wishlist.html")
(def test-wishlist-url "https://www.amazon.de/hz/wishlist/ls/2Y2U31UCNA1ME")

(deftest ^:amazon import-batch-test
  (is (import-batch test-wishlist-url)))