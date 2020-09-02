(ns amazon.books.single-book-parser-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as spec]
            [amazon.books.single-book-parser :refer [load-book parse-html]]))

; todo: maybe change the ns name, and move this spec to that namespace then
(spec/def :amazon.books/book
  (spec/keys :req [:amazon.books/title
                   :amazon.books/authors
                   :amazon.books/amazon-book-image-front]
             :opt [:amazon.books/isbn-10
                   :amazon.books/isbn-13
                   :amazon.books/publisher
                   :amazon.books/language
                   :amazon.books/book-length
                   :amazon.books/amazon-format]))

(def single-book-html-01
  "https://www.amazon.de/dp/0198779291/?coliid=IVUEOO0EISPOI&colid=13XXXLP6RR1X9&psc=1&ref_=lv_ov_lig_dp_it"
  (slurp "src/test/resources/amazon/single_books/01-madness-and-modernism.html"))
(def single-book-html-02
  (slurp "src/test/resources/amazon/single_books/02-now-you-see-it.html"))

; todo: add tests first for the given HTMLs above, then also for the direct amazon links
(deftest single-book-load-test
  (testing "book 01"
    (let [book-01 (parse-html single-book-html-01)]
      (is (spec/valid? :amazon.books/book book-01))
      (is (= #:amazon.books {:title                   "Madness and Modernism: Insanity in the light of modern art, literature, and thought (revised edition) (International Perspectives in Philosophy and Psychiatry) Taschenbuch – 31. Oktober 2017",
                             :authors                 ["Louis Sass"],
                             :amazon-book-image-front "https://images-na.ssl-images-amazon.com/images/I/418HwSEi9DL._SY344_BO1,204,203,200_.jpg",
                             :book-length             "556 Seiten",
                             :isbn-10                 "0198779291",
                             :isbn-13                 "978-0198779292",
                             :publisher               "Oxford University Press; Revised Auflage (31. Oktober 2017)",
                             :language                "Englisch"}
             book-01))))
  (testing "book 02"
    (let [book-02 (parse-html single-book-html-02)]
      (is (spec/valid? :amazon.books/book book-02))
      ; Todo: Fix book 02
      ;  In the link, switch from Kindle to notebook, then get the information there. Also check that it gets the
      ;  correct information, since, as seen below, it does not do that for the Kindle version (see the commented
      ;  sections)
      (is (= #:amazon.books {:title                   "Now You See It and Other Essays on Design (English Edition) Kindle Ausgabe",
                             ;:authors                 ["Entdecken Sie Michael Bierut bei Amazon"
                             ;                          "Suchergebnisse"
                             ;                          "Erfahren Sie mehr über Author Central"
                             ;                          "Michael Bierut"],
                             :amazon-book-image-front "https://m.media-amazon.com/images/I/41vZOzm8+aL._SY346_.jpg",
                             ;:amazon-format           "Kindle Ausgabe",
                             :book-length             "1616896248",
                             :publisher               "Princeton Architectural Press (12. März 2019)",
                             :language                "Englisch"}
             book-02)))))

(parse-html single-book-html-01)


(def link-02
  "https://www.amazon.de/Michael-Bierut-ebook/dp/B07P89NHMP/ref=tmm_kin_swatch_0?_encoding=UTF8&qid=1599046696&sr=8-34")

;(load-book link-02)