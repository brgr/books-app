(ns amazon.search.parse.authors
  (:require
    [schema.core :as s]
    [clojure.string :as str]))

(s/defn get-authors-from-metadata-below-title :- [s/Str]
  [metadata-below-title :- s/Str]
  (if metadata-below-title
    (when-let [authors-plain-text (as-> (str/split metadata-below-title #"\|") data
                                        (map str/trim data)
                                        (filter #(str/starts-with? % "von ") data)
                                        (first data))]
      (-> (subs authors-plain-text 4)
          (str/split #" und ")))))
