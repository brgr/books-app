(ns amazon.books.single-book-parser
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [amazon.books.dynamic-site-fetch :as amazon-fetch])
  (:import [org.jsoup Jsoup]))

(defn- split-information [information]
  (let [[name info] (str/split information #": ")
        name (cond
               (str/includes? name "Format") :amazon-format
               (str/includes? name "Seitenzahl") :book-length
               (str/includes? name "ISBN") :isbn
               (str/includes? name "Verlag") :publisher
               (str/includes? name "Sprache") :language
               :else nil)]
    (if (nil? name)
      nil
      {name info})))

(defn- information [informations]
  (let [informations (filter #(str/includes? % ": ") informations)]
    (into {} (->> (map split-information informations) (filter not-empty)))))

(defn- parse-html [single-book-html]
  (let [soup (Jsoup/parse single-book-html)
        product-information (information (.eachText (-> (.select soup "#productDetailsTable") (.select ".content > ul li"))))]
    (into {:title                   (->> (.select soup "#title") (.text))
           ; todo: does it work with multiple authors?
           :authors                 (.eachText (-> (.select soup ".author") (.select ".notFaded") (.select ".contributorNameID")))
           :amazon-book-image-front (.attr (.select soup "#ebooksImgBlkFront") "src")
           ; todo: try to get the description - doesn't work because of iframe
           :description             (.text (.select soup "#bookDesc_iframe_wrapper"))}
          product-information)))

(defn load-book [url]
  (let [url (if (str/includes? url "amazon.de") url (str "https://amazon.de" url))
        html (amazon-fetch/get-single-book-html url true)]
    (parse-html html)))