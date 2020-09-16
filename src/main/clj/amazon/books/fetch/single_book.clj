(ns amazon.books.fetch.single-book
  (:require [clojure.string :as str]
            [etaoin.api :refer :all]))

(defn- scroll-to-when-exists [driver book-to-select]
  (try (scroll-query driver [{:id :twister}
                             {:tag :span :fn/text book-to-select}])
       true
       (catch Exception _
         false)))

(defn- select-physical-book [driver]
  (let [physical-book (loop [books-to-try ["Broschiert" "Taschenbuch" "Gebundenes Buch"]]
                        (if (scroll-to-when-exists driver (first books-to-try))
                          (first books-to-try)
                          (if (empty? (rest books-to-try))
                            nil
                            (recur (rest books-to-try)))))]
    (click driver [{:id :twister}
                   {:tag :span :fn/text physical-book}])))



(defn- go-to-single-book [driver single-book-url]
  (go driver single-book-url)
  (wait driver 1)
  (when (visible? driver {:id :showMoreFormatsPrompt})
    (click driver {:id :showMoreFormatsPrompt}))
  (let [text-of-selected-swatch (get-element-text driver [{:id :twister}
                                                          {:tag :div :fn/has-classes [:top-level :selected-row]}])]
    (when (or (str/includes? text-of-selected-swatch "Kindle")
              (str/includes? text-of-selected-swatch "Audible"))
      (select-physical-book driver))))

(defn get-single-book-html [single-book-url headless?]
  (let [driver (firefox {:headless headless?})]
    (try
      (go-to-single-book driver single-book-url)
      ; This wait is needed for the iframe to appear always (otherwise it fails sometimes)
      (wait driver 2)
      (let [final-url (get-url driver)
            outer-frame-html (get-source driver)
            description-frame-html (with-frame driver {:id :bookDesc_iframe}
                                               (get-source driver))]
        [outer-frame-html description-frame-html final-url])
      (finally
        (quit driver)))))