(ns quick-actions
  (:require [bookstore.db.model :as db]))

(comment
  (db/remove-all-books))

(comment
  (def books
    (read-string (slurp "mongo/src/main/resources/books_template.clj")))
  (-> books)
  (db/insert-new-books books))


(comment

  (def faulty-books
    (read-string (slurp "mongo/resources/books_from_wishlist_faulty.clj")))

  (count faulty-books)
  (def books-to-retry
    (->> faulty-books
         (group-by :status)
         :to-retry))

  ; todo: load the images for these books, then remove the error in them and use them for working on the frontend
  (def faulty-books-that-actually-worked
    (->> faulty-books
         (group-by :status)
         :error
         (filter #(contains? % :books.book/amazon-book-image-front))))

  (def really-faulty-books
    (->> faulty-books
         (group-by :status)
         :error
         (filter #(not (contains? % :books.book/amazon-book-image-front)))))

  ; fixme: the book I am currently reading (Das Versprechen) is not loaded...!
  (->> really-faulty-books
       (map :books.book/title)
       (filter #(clojure.string/includes? % "Versprechen")))


  ; === images ===
  ; the code below is for checking/moving the images

  (def images-from-loaded-books
    (->> books
         (map :books.book/amazon-book-image-front)
         (filter not-empty)
         (map #(subs % (+ 1 (clojure.string/last-index-of % "/"))))
         set))

  (map #(clojure.java.io/copy %1 %2)
    (->> images-from-loaded-books
         (map #(clojure.java.io/file (str "src/main/resources/public/img/front_matter/" %))))
    (->> images-from-loaded-books
         (map #(clojure.java.io/file (str "mongo/src/main/resources/front_matters/" %)))))
  ; todo: they are added now - use Git LFS to add them to git

  (def all-loaded-images
    (->> (file-seq (clojure.java.io/file "src/main/resources/public/img/front_matter"))
         rest
         (map str)
         (map #(subs % (+ 1 (clojure.string/last-index-of % "/"))))
         set))

  (->> (clojure.set/difference all-loaded-images images-from-loaded-books)
       count)

  ; todo: there are more images loaded! are the ones from faulty probably loaded as well?
  ;  --> if so, load them and add them! :-)
  )