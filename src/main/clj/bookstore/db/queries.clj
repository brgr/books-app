(ns bookstore.db.queries
  "This namespace contains wrappers of the SQL scripts, s.t. error hinting outside this namespace works correctly.

   While the original idea was to just have a very plain wrapper over the database calls, we have decided to be
   pragmatic and perform minor operations already in this file.

   Currently, this means that for some database operations, we already only expect concrete values instead of full
   maps. We then create the maps on the go.
   Where this is not feasible because of too many values, we at least expect the keywords in the given map to be in
   kebab-case and only here perform it to snake_case."
  (:require
    [bookstore.db.core :as db]
    [camel-snake-kebab.extras :refer [transform-keys]]
    [camel-snake-kebab.core :refer [->snake_case_keyword]])
  (:import
    (java.time LocalDateTime)))

(defn- transform-keys-and-insert
  "A utility function that transforms normal kebab-case keys into snake_case keys, and then performs the given
   database call."
  [sql-target-function map]
  (sql-target-function (transform-keys ->snake_case_keyword map)))

(defn create-author!
  [full-name]
  (db/create-author! {:full_name full-name}))

(defn create-publisher!
  [full-name]
  (db/create-publisher! {:full_name full-name}))

(defn create-book!
  "Merges the given book with an empty book. Note that the empty book has however neither :title nor :added given,
   as these are needed to be given by the user of this call.

   Note that the keys of the given book are expected in kebab-case."
  [book]
  (transform-keys-and-insert
    db/create-book!
    (merge
      {:subtitle               nil
       :fk-publisher           nil
       :asin                   nil
       :isbn-10                nil
       :isbn-13                nil
       :language               nil
       :cover-image-id         nil
       :weight                 nil
       :price                  nil
       :edition-name           nil
       :number-of-pages        nil
       :physical-dimensions    nil
       :physical-format        nil
       :publish-country        nil
       :publish-date           nil
       :publish-date-precision nil
       :description            nil
       :notes                  nil
       :last-modified          nil}
      book)))

(defn create-book-author!
  [book-id author-id]
  (db/create-book-author! {:book_id book-id, :publisher_id author-id}))

;; todo: now that these are prepared, I want to first add all books that we already have (doing that we might still learn
;;  some stuff on the way...); then we can make it work for the old DB scripts, i.e. make it work with the API
; to generify these, we might want to add these queries here into a single one? Or how do we do it?
; Do we also want to use spec or scheme?

(defn create-full-book!
  [{:keys [author_full_name
           publisher_full_name
           title subtitle asin isbn_10 isbn_13 language cover_image_id weight price edition_name
           number_of_pages physical_dimensions physical_format publish_country publish_date publish_date_precision
           description notes added last_modified]}]
  (db/create-full-book!
    {:author_full_name    author_full_name,
     :publisher_full_name publisher_full_name,
     :title               title, :subtitle subtitle, :asin asin, :isbn_10 isbn_10, :isbn_13 isbn_13, :language language, :cover_image_id cover_image_id, :weight weight, :price price, :edition_name edition_name,
     :number_of_pages     number_of_pages, :physical_dimensions physical_dimensions, :physical_format physical_format, :publish_country publish_country, :publish_date publish_date, :publish_date_precision publish_date_precision,
     :description         description, :notes notes, :added added, :last_modified last_modified}))

(comment

  ; This works now! :-)
  (create-full-book!
    {:author_full_name       "Jemand",
     :publisher_full_name    "asdf",
     :title                  "Titel 1",
     :subtitle               nil,
     :asin                   nil,
     :isbn_10                nil,
     :isbn_13                nil,
     :language               nil,
     :cover_image_id         nil,
     :weight                 nil,
     :price                  nil,
     :edition_name           nil,
     :number_of_pages        nil,
     :physical_dimensions    nil,
     :physical_format        nil,
     :publish_country        nil,
     :publish_date           nil,
     :publish_date_precision nil,
     :description            nil,
     :notes                  nil,
     :added                  (LocalDateTime/now),
     :last_modified          nil})

  ; TODO:
  ; This transaction works; Now, let's clean it up!
  (conman.core/with-transaction [db/*db* {}]
    (let [author-name "Autor6"
          publisher-name "Publisher1"
          added-author (first (create-author! author-name))
          added-publisher (first (create-publisher! publisher-name))
          author-data (or added-author (db/get-author-by-name {:full_name author-name}))
          publisher-data (or added-publisher (db/get-publisher-by-name {:full_name publisher-name}))
          added-book (first (create-book! {:title "Book3", :added (LocalDateTime/now)}))]
      (println "Author id: " (:id author-data)
               "\nPublisher id:" (:id publisher-data)
               "\nBook:" added-book
               "\nBook id:" (:id added-book))
      (db/create-book-publisher! {:book_id (:id added-book), :publisher_id (:id publisher-data)})
      (db/create-book-author! {:book_id (:id added-book), :author_id (:id author-data)})
      (println "Author:" author-data
               "\nPublisher:" publisher-data
               "\nBook:" added-book)))
  )