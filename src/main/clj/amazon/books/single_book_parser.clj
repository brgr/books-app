(ns amazon.books.single-book-parser
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [amazon.books.dynamic-site-fetch :as amazon-fetch])
  (:import [org.jsoup Jsoup]))

(defn- split-information [information]
  (let [[name info & rest] (str/split information #":")
        key (cond
              (str/includes? name "Format") :amazon.books/amazon-format
              ; It is important that ISBN is before Seitenzahl - because the name field of the ISBN contains also the
              ; text Seitenzahl, it would otherwise save it as :book-length
              (str/includes? name "ISBN-10") :amazon.books/isbn-10
              (str/includes? name "ISBN-13") :amazon.books/isbn-13
              (str/includes? name "Seitenzahl") :amazon.books/book-length
              (str/includes? name "Taschenbuch") :amazon.books/book-length
              (str/includes? name "Gebundene Ausgabe") :amazon.books/book-length
              (str/includes? name "Verlag") :amazon.books/publisher
              (str/includes? name "Herausgeber") :amazon.books/publisher
              (str/includes? name "Sprache") :amazon.books/language
              :else nil)]
    (cond
      ;Unfortunately, Amazon has an error here, it writes e.g.: "Sprache: : Englisch" (with 2 colons!)
      (and (str/includes? name "Sprache")
           (not (empty? rest))) {:amazon.books/language (-> (first rest) (str/trim))}
      (or (nil? key) (nil? info)) nil
      :else {key (str/trim info)})))

(defn- parse-informations [informations]
  (let [informations (filter #(str/includes? % ": ") informations)]
    (into {} (->> (map split-information informations)
                  (filter not-empty)))))

(defn- product-informations [soup]
  (let [informations-table (if-let [table (not-empty (.select soup "#productDetailsTable .content > ul li"))]
                             table
                             (.select soup "#detailBulletsWrapper_feature_div > #detailBullets_feature_div > ul li"))]
    (-> (.eachText informations-table)
        (parse-informations))))

(defn- book-image-front [soup]
  (if-let [book-image-front (not-empty (-> (.select soup "#ebooksImgBlkFront")
                                           (.attr "src")))]
    book-image-front
    (-> (.select soup "#imgBlkFront")
        (.attr "data-a-dynamic-image")
        (json/read-str)
        (first)
        (first))))

(defn- authors [soup]
  (let [authors (-> (.select soup ".author")
                    (.select ".notFaded")
                    (.select ".a-link-normal"))
        each-author (-> (.select authors ".contributorNameID")
                        (.eachText))]
    (if (empty? each-author)
      (.eachText authors)
      each-author)))

(defn parse-html [single-book-html]
  (let [soup (Jsoup/parse single-book-html)
        product-information (product-informations soup)
        title (->> (.select soup "#title")
                   (.text))
        authors (authors soup)
        amazon-book-image-front (book-image-front soup)]
    (into {:amazon.books/title                   title
           ; todo: does it work with multiple authors?
           :amazon.books/authors                 authors
           :amazon.books/amazon-book-image-front amazon-book-image-front}
          product-information)))

(defn parse-description [description-frame-html]
  (let [soup (Jsoup/parse description-frame-html)]
    (->> (.select soup "#iframeContent") (.text))))

(defn load-book [url]
  (let [url (if (str/includes? url "amazon.de")
              url
              (str "https://amazon.de" url))
        [outer-frame-html description-frame-html] (amazon-fetch/get-single-book-html url true)]
    (into (parse-html outer-frame-html)
          {:amazon.books/description (parse-description description-frame-html)})))