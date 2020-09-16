(ns bookstore.files.file-management
  (:require [clojure.string :as str]))

(defn get-file-name [uri]
  (let [last-slash (str/last-index-of uri "/")]
    (subs uri (+ 1 last-slash))))