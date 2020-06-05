(ns amazon.books.list-parser-test
  (:require [clojure.test :refer :all]
            [amazon.books.list-parser :as list-parser]))

(def parsed-content
  (->> (slurp "src/test/resources/whole.html")
       (list-parser/load-books-from-amazon-wishlist-html)))

(deftest second-book-is-correct
  (is (= (->> parsed-content (second))
         {:amazon-id     "IONL7ZDL5YEP7",
          :title         "In Search of Respect: Selling Crack in El Barrio Second Edition (Structural Analysis in the Social Sciences, Band 10)",
          :author        "Philippe Bourgois (Taschenbuch)",
          :amazon-url    "/dp/0521017114/?coliid=IONL7ZDL5YEP7&colid=13XXXLP6RR1X9&psc=1&ref_=lv_vv_lig_dp_it",
          :thumbnail     "https://images-na.ssl-images-amazon.com/images/I/61HycdeGrGL._SS135_.jpg",
          :itemAddedDate "Hinzugefügt am 1. April 2020",
          :price         "14,64 €"})))

(deftest count-is-correct
  (is (= (-> parsed-content (count)) 460)))



