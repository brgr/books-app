(ns bookstore.db.queries
  "This namespace contains wrappers of the SQL scripts, s.t. error hinting outside of this namespace works correctly"
  (:require [bookstore.db.core :as db])
  (:import (java.time LocalDateTime)))

(defn create-author!
  [full-name]
  (db/create-author! {:full_name full-name}))

(defn create-publisher!
  [full-name]
  (db/create-publisher! {:full_name full-name}))

(defn create-book!
  [title subtitle asin isbn-10 isbn-13 language cover-image-id weight price edition-name number-of-pages
   physical-dimensions physical-format publish-country publish-date publish-date-precision description notes added
   last-modified]
  (db/create-book!
    {:title                  title
     :subtitle               subtitle
     :asin                   asin
     :isbn_10                isbn-10
     :isbn_13                isbn-13
     :language               language
     :cover_image_id         cover-image-id
     :weight                 weight
     :price                  price
     :edition_name           edition-name
     :number_of_pages        number-of-pages
     :physical_dimensions    physical-dimensions
     :physical_format        physical-format
     :publish_country        publish-country
     :publish_date           publish-date
     :publish_date_precision publish-date-precision
     :description            description
     :notes                  notes
     :added                  added
     :last_modified          last-modified}))

(defn create-book-publisher!
  [book-id publisher-id]
  (db/create-book-publisher! {:book_id book-id, :publisher_id publisher-id}))

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
  )