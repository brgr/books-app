(ns amazon.search.parse.authors
  (:require
    [schema.core :as s]
    [clojure.string :as str]))

(s/defn get-authors-from-metadata-below-title :- [s/Str]
  [metadata-below-title :- s/Str]
  (as-> (str/split metadata-below-title #"\|") data
        (map str/trim data)
        (filter #(str/starts-with? % "von ") data)
        (first data)
        (subs data 4)
        (str/split data #" und ")))
