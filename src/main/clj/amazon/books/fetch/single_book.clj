(ns amazon.books.fetch.single-book
  (:require [clojure.string :as str]
            [etaoin.api :refer :all]
            [amazon.books.fetch.scraping.driver :refer [get-driver]]))

(def accepted-formats ["Broschiert" "Taschenbuch" "Gebundenes Buch"])

(defn- scroll-to-when-exists [driver book-to-select]
  (try (scroll-query driver [{:id :twister}
                             {:tag :span :fn/text book-to-select}])
       true
       (catch Exception _
         false)))

(defn- select-physical-book [driver]
  (let [physical-book (loop [books-to-try accepted-formats]
                        (if (scroll-to-when-exists driver (first books-to-try))
                          (first books-to-try)
                          (if (empty? (rest books-to-try))
                            nil
                            (recur (rest books-to-try)))))]
    (click driver [{:id :twister}
                   {:tag :span :fn/text physical-book}])))

(defn- switch-book-format [driver]
  (when (visible? driver {:id :showMoreFormatsPrompt})
    (click driver {:id :showMoreFormatsPrompt}))
  (let [text-of-selected-swatch (get-element-text driver [{:id :twister}
                                                          {:tag :div :fn/has-classes [:top-level :selected-row]}])]
    (when (or (str/includes? text-of-selected-swatch "Kindle")
              (str/includes? text-of-selected-swatch "Audible"))
      (select-physical-book driver))))

(defn current-format-selected [driver]
  (try (get-element-text driver [{:id :tmmSwatches}
                                 ; The following is a workaround, because otherwise "unselected" would be found as well
                                 "//li[contains(concat(' ',normalize-space(@class),' '),' selected ')]"])
       (catch Exception _
         nil)))

(defn str-contains-any? [s l]
  (and s (->> (map #(str/includes? s %) l)
              (some true?))))

(defn fetch-book-description [driver]
  (wait-visible driver {:tag :iframe :id :bookDesc_iframe})
  (with-frame driver {:id :bookDesc_iframe}
              (get-source driver)))

(defn fetch-single-book-site [driver single-book-url fetch-book-description?]
  (go driver single-book-url)
  (wait-visible driver {:tag :div :id :tmmSwatches})
  (when (not (str-contains-any? (current-format-selected driver) accepted-formats))
    (switch-book-format driver))
  (let [final-url (get-url driver)
        outer-frame-html (get-source driver)
        description-frame-html (when fetch-book-description? (fetch-book-description driver))]
    [outer-frame-html description-frame-html final-url]))

(defn get-single-book-html [single-book-url headless?]
  (let [driver (get-driver headless?)]
    (try
      (with-wait-timeout 30
        (fetch-single-book-site driver single-book-url true))
      (catch Exception e
        ; todo: move this exception handling further down!
        (when (and (= (:type (ex-data e)) :etaoin/timeout)
                   (str/includes? (:message (ex-data e)) ":bookDesc_iframe"))
          (fetch-single-book-site driver single-book-url false)))
      (finally
        (quit driver)))))