(ns amazon.books.dynamic-site-fetch-test
  (:require [clojure.test :refer :all]
            [amazon.books.dynamic-site-fetch :as amazon]
            [clojure.string :as str]))

(def test-wishlist-local "src/test/resources/test-wishlist.html")
(def test-wishlist-url "https://www.amazon.de/hz/wishlist/ls/2Y2U31UCNA1ME")

; :amazon test needs to be explicitly called in leiningen:
; lein test :amazon
(deftest ^:amazon can-access-amazon-wishlist?
  (is (as-> (amazon/get-wishlist-html test-wishlist-url true) html
         (and
           (str/includes? html "Coders: The Making of a New Tribe and the Remaking of the World (English Edition)")
           (str/includes? html "Now You See It and Other Essays on Design (English Edition)")
           (str/includes? html "Madness and Modernism: Insanity in the light of modern art, literature, and thought (revised edition) (International Perspectives in Philosophy and Psychiatry)")
           (str/includes? html "Rückkehr nach Reims (edition suhrkamp)")
           (str/includes? html "Man's Search for Meaning by Frankl, Viktor E. (2006) Taschenbuch")
           (str/includes? html "The Art of Statistics: Learning from Data (Pelican Books) (English Edition)")))))
