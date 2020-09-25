(ns amazon.books.parse.single-book
  (:require [clojure.string :as str]
            [clojure.data.json :as json]
            [amazon.books.fetch.single-book :as single-book])
  (:import [org.jsoup Jsoup]))

(defn- split-information [information]
  (let [[name info & rest] (str/split information #":")
        key (cond
              (str/includes? name "Format") :books.book/amazon-format
              ; It is important that ISBN is before Seitenzahl - because the name field of the ISBN contains also the
              ; text Seitenzahl, it would otherwise save it as :book-length
              (str/includes? name "ISBN-10") :books.book/isbn-10
              (str/includes? name "ISBN-13") :books.book/isbn-13
              (str/includes? name "Seitenzahl") :books.book/book-length
              (str/includes? name "Taschenbuch") :books.book/book-length
              (str/includes? name "Gebundene Ausgabe") :books.book/book-length
              (str/includes? name "Verlag") :books.book/publisher
              (str/includes? name "Herausgeber") :books.book/publisher
              (str/includes? name "Sprache") :books.book/language
              :else nil)]
    (cond
      ; Unfortunately, Amazon has an error here, it writes e.g.: "Sprache: : Englisch" (with 2 colons!)
      (and (str/includes? name "Sprache")
           (not (empty? rest))) {:books.book/language (-> (first rest) (str/trim))}
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


; Fixme: Amazon does not always include the class .contributorNameID for authors, even though it writes "Autor" next to
;  the name. To fix this, one would need to read the text that is written in brackets next to the name, instead of
;  relying on class names. Currently, some authors may be missed using just this approach.
(defn- authors [soup]
  (let [authors (-> (.select soup "span.author a.a-link-normal.contributorNameID")
                    (.eachText))]
    (if (empty? authors)
      (-> (.select soup "span.author a.a-link-normal")
          (.eachText))
      authors)))

(defn parse-html [single-book-html]
  (let [soup (Jsoup/parse single-book-html)
        product-information (product-informations soup)
        title (->> (.select soup "#title")
                   (.text))
        authors (authors soup)
        amazon-book-image-front (book-image-front soup)]
    (into {:books.book/title                   title
           :books.book/authors                 authors
           :books.book/amazon-book-image-front amazon-book-image-front}
          product-information)))

(defn parse-description [description-frame-html]
  (when (not-empty description-frame-html)
    (let [soup (Jsoup/parse description-frame-html)]
     (->> (.select soup "#iframeContent") (.text)))))

(defn load-book [url]
  (let [url (if (str/includes? url "amazon.de")
              url
              (str "https://amazon.de" url))
        [outer-frame-html description-frame-html final-url] (single-book/get-single-book-html url true)]
    (into (parse-html outer-frame-html)
          {:books.book/description (parse-description description-frame-html)
           :books.book/amazon-url final-url})))