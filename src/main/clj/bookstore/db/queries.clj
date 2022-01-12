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
    [camel-snake-kebab.core :refer [->snake_case_keyword]]))

(defn- transform-keys-and-insert
  "A utility function that transforms normal kebab-case keys into snake_case keys, and then performs the given
   database call."
  [sql-target-function map]
  (sql-target-function (transform-keys ->snake_case_keyword map)))

(defn create-author!
  [full-name]
  (db/create-author! {:full_name full-name}))

(defn get-author-by-name
  [full-name]
  (db/get-author-by-name {:full_name full-name}))

(defn create-publisher!
  [full-name]
  (db/create-publisher! {:full_name full-name}))

(defn get-publisher-by-name
  [full-name]
  (db/get-publisher-by-name {:full_name full-name}))

(defn create-book!
  "Merges the given book with an empty book. Note that the empty book has however neither :title nor :added given,
   as these are needed to be given by the user of this call.

   Note that the keys of the given book are expected in kebab-case."
  [book & [publisher-id]]
  (transform-keys-and-insert
    db/create-book!
    (merge
      {:subtitle               nil
       :fk-publisher           publisher-id
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
  (db/create-book-author! {:book_id book-id, :author_id author-id}))

(defn get-first-n-books
  [n]
  (db/get-first-n-books {:n n}))
