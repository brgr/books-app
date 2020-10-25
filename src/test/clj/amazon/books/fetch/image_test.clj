(ns amazon.books.fetch.image-test
  (:require [clojure.test :refer :all])
  (:require [bookstore.files.file-management :refer [get-file-name]]))

(deftest get-file-name-test
  (is (= "filename.jpg"
         (get-file-name "www.example.com/test/filename.jpg")))
  (is (thrown? NullPointerException (get-file-name "www.example.com?no-file=true"))))
