(ns amazon.books.fetch.single-book
  (:require [clojure.string :as str]
            [etaoin.api :refer :all]))

(defn- go-to-single-book [driver single-book-url]
  (go driver single-book-url)
  (if (exists? driver {:id :twister})
    (let [text-of-selected-swatch (get-element-text driver [{:id :twister}
                                                            {:tag :div :fn/has-classes [:top-level :selected-row]}])]
      (when (str/includes? text-of-selected-swatch "Kindle")
        (if (visible? driver {:id :showMoreFormatsPrompt})
          (click driver {:id :showMoreFormatsPrompt}))
        (if (visible? driver {:id :showMoreFormatsPrompt}) (click driver {:id :showMoreFormatsPrompt}))
        (click driver [{:id :twister}
                       {:tag :div :fn/has-classes [:top-level :unselected-row]}])))))

(defn get-single-book-html [single-book-url headless?]
  (let [driver (firefox {:headless headless?})]
    (try
      (go-to-single-book driver single-book-url)
      ; This wait is needed for the iframe to appear always (otherwise it fails sometimes)
      (wait driver 1)
      (let [outer-frame-html (get-source driver)
            description-frame-html (with-frame driver {:id :bookDesc_iframe}
                                               (get-source driver))]
        [outer-frame-html description-frame-html])
      (finally
        (quit driver)))))