(ns amazon.books.fetch.single-book
  (:require [clojure.string :as str]
            [etaoin.api :refer :all]))

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
  (get-element-text driver [{:id :tmmSwatches}
                            ; The following is a workaround, because otherwise "unselected" would be found as well
                            "//li[contains(concat(' ',normalize-space(@class),' '),' selected ')]"]))

(defn str-contains-any? [s l]
  (->> (map #(str/includes? s %) l)
       (some true?)))

(defn get-single-book-html [single-book-url headless?]
  (let [driver (firefox {:headless headless?})]
    (try
      (go driver single-book-url)
      (wait driver 1)
      (when (not (str-contains-any? (current-format-selected driver) accepted-formats))
        (switch-book-format driver))
      ; This wait is needed for the iframe to appear always (otherwise it fails sometimes)
      (wait driver 2)
      (let [final-url (get-url driver)
            outer-frame-html (get-source driver)
            description-frame-html (with-frame driver {:id :bookDesc_iframe}
                                               (get-source driver))]
        [outer-frame-html description-frame-html final-url])
      (finally
        (quit driver)))))