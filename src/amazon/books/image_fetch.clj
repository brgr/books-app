(ns amazon.books.image-fetch
  (:require [clojure.string :as str]
            [clojure.java.io :as io]))

(defn get-file-name [uri]
  (let [last-slash (str/last-index-of uri "/")]
    (subs uri (+ 1 last-slash))))

(defn load-file-from
  ([uri output-path]
   (let [filename (get-file-name uri)
         filepath (str output-path filename)]
     (do
       (println "filename:" filename)
       (println "filepath:" filepath)
       (with-open [in (io/input-stream uri)
                  out (io/output-stream filepath)]
        (io/copy in out)))))
  ; todo: maybe remove
  ([uri] (load-file-from uri "resources/test_wishlist/book_images/")))


(defn load-book-images [books directory-path]
  (->> (map :thumbnail books)
       ; TODO: In my real wishlist this was needed - why?
       (filter some?)
       (map load-file-from)))


;; Note: For my wishlist, it somehow stopped at the 230th or so to download... Idk why
;; todo: Get via the files all that are downloaded already, check
;;  them with the books and download the rest!
;;  Probably find out why it didn't download them all.
;
;; ----> use this:
;(file-seq (io/file "resources/book_images"))
