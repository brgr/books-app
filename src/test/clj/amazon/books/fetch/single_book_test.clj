(ns amazon.books.fetch.single-book-test
  (:require [clojure.test :refer :all])
  (:require [amazon.books.fetch.single-book :refer [get-single-book-html]]))

(def kindle-url "https://www.amazon.de/dp/B07P89NHMP/?coliid=IGECR2MGXNLUO&colid=2Y2U31UCNA1ME&psc=0&ref_=lv_vv_lig_dp_it")

(deftest go-to-single-book-test
  (testing "change book format to something that is not Kindle"
    (let [[_ _ final-url] (get-single-book-html kindle-url true)]
      (is (not= kindle-url final-url)))))
