(ns amazon.search.parse.authors-test
  (:require [clojure.test :refer :all])
  (:require [amazon.search.parse.authors :refer [get-authors-from-metadata-below-title]]))

(deftest get-authors-from-metadata-below-title-test
  (assert (= ["Soetsu Yanagi" "Michael Brase"]
             (get-authors-from-metadata-below-title
               "Englisch Ausgabe  |  von Soetsu Yanagi und Michael Brase  |  8. Oktober 2019"))))
