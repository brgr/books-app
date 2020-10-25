(ns amazon.books.fetch.single-book
  (:require [clojure.string :as str]
            [etaoin.api :refer :all]
            [amazon.books.fetch.scraping.driver :refer [get-driver]]))

(def accepted-formats ["Broschiert" "Taschenbuch" "Gebundenes Buch" "Paperback" "Hardcover"])

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
  (try
    (wait-visible driver {:tag :div :id :tmmSwatches})
    (get-element-text driver [{:id :tmmSwatches}
                              ; The following is a workaround, because otherwise "unselected" would be found as well
                              "//li[contains(concat(' ',normalize-space(@class),' '),' selected ')]"])
    (catch Exception _
      nil)))

(defn str-contains-any? [s l]
  (->> (map #(str/includes? s %) l)
       (some true?)))

(defn fetch-book-description [driver]
  (try (do
         (wait-visible driver {:tag :iframe :id :bookDesc_iframe})
         (with-frame driver {:id :bookDesc_iframe}
                     (get-source driver)))
       (catch Exception _                                   ; when there is no book description it throws a timeout
         nil)))

(defn switch-book-format-if-needed [driver]
  (let [selected-format (current-format-selected driver)]
    (when (and (not-empty selected-format)
               (not (str-contains-any? selected-format accepted-formats)))
      (switch-book-format driver))))

(defn fetch-single-book-site [driver single-book-url fetch-book-description?]
  (go driver single-book-url)
  (switch-book-format-if-needed driver)
  (if (visible? driver {:tag :div :id :dp-container})
    (let [final-url (get-url driver)
          outer-frame-html (get-source driver)
          description-frame-html (when fetch-book-description? (fetch-book-description driver))]
      {:outer-frame-html outer-frame-html
       :description-frame-html description-frame-html
       :final-url final-url})
    nil))

(defn get-single-book-html [single-book-url]
  (let [driver (get-driver)]
    (try
      (with-wait-timeout 30
        (fetch-single-book-site driver single-book-url true))
      (finally
        (quit driver)))))