(ns amazon.books.single-book-parser
  (:require [clojure.pprint :refer :all]
            [clojure.string :as str]
            [clojure.data.json :as json]
            [amazon.books.dynamic-site-fetch :as amazon-fetch])
  (:import [org.jsoup Jsoup]))

(defn- split-information [information]
  (let [[name info] (str/split information #":")
        name (cond
               (str/includes? name "Format") :amazon-format
               ; it is important that ISBN is before Seitenzahl - because the name field of the ISBN contains also the
               ; text Seitenzahl, it would otherwise save it as :book-length
               (str/includes? name "ISBN") :isbn
               (str/includes? name "Seitenzahl") :book-length
               (str/includes? name "Verlag") :publisher
               (str/includes? name "Sprache") :language
               :else nil)]
    (println name info)
    (if (nil? name)
      nil
      {name info})))

(defn- information [informations]
  (let [informations (filter #(str/includes? % ": ") informations)]
    (into {} (->> (map split-information informations)
                  (filter not-empty)))))

(defn- book-image-front [soup]
  (if-let [book-image-front (not-empty (-> (.select soup "#ebooksImgBlkFront")
                                           (.attr "src")))]
    book-image-front
    (-> (.select soup "#imgBlkFront")
        (.attr "data-a-dynamic-image")
        (json/read-str)
        (first)
        (first))))

(defn parse-html [single-book-html]
  (let [soup (Jsoup/parse single-book-html)
        product-information (information (.eachText (-> (.select soup "#productDetailsTable") (.select ".content > ul li"))))]
    (into {:title                   (->> (.select soup "#title") (.text))
           ; todo: does it work with multiple authors?
           :authors                 (.eachText (-> (.select soup ".author") (.select ".notFaded") (.select ".a-link-normal")))
           :amazon-book-image-front (book-image-front soup)}
          product-information)))

(defn parse-description [description-frame-html]
  (let [soup (Jsoup/parse description-frame-html)]
    (->> (.select soup "#iframeContent") (.text))))

(defn load-book [url]
  ; todo: I need to go to the Hardcover / Notebook page (i.e., not the Kindle page) s.t. I can get an ISBN! Kindle
  ;  documents do not have an ISBN
  (let [url (if (str/includes? url "amazon.de") url (str "https://amazon.de" url))
        [outer-frame-html description-frame-html] (amazon-fetch/get-single-book-html url true)]
    (into (parse-html outer-frame-html)
          {:description (parse-description description-frame-html)})))
