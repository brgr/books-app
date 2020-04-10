(ns amazon.books.image-fetch-test
  (:require [clojure.test :refer :all])
  (:require [amazon.books.image-fetch :refer [get-file-name]]))

(deftest get-file-name-test
  (is (= "filename.jpg"
         (get-file-name "www.example.com/test/filename.jpg")))
  (is (thrown? NullPointerException (get-file-name "www.example.com?no-file=true"))))
