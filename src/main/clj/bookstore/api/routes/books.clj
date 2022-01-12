(ns bookstore.api.routes.books
  (:require
    [schema.core :as s]
    #_#_[bookstore.db.model :as bookstore]
        [bookstore.db.update]
    [bookstore.db.queries :as queries]
    [clojure.java.io :as io]
    [bookstore.files.file-management :refer [get-file-name]])
  (:import (java.time LocalDateTime)))

;; TODO: I can now start changing stuff here!
;; I need to go over these and fix them one by one. Most probably all of them won't work anymore, as for now
;; I don't have a single SELECT query. I need to see what kind of queries I need, add them first in SQL, then finally
;; call them here.
;; Like this, I should be able to slowly make the app work again.

(def books-routes
  ["/books"
   ; Todo: use a Schema to specify what is returned!
   ; Fixme: As a workaround, only loads the first 30 books instead of all books, s.t. the frontend is loaded faster
   ;  This should be fixed with real pagination (BOOKS-9)
   {:swagger {:tags ["books"]}
    :get     {:summary "Returns the first 30 books that are in the DB currently"
              :status  200
              :handler (fn [_]
                         {:status 200
                          :body   {:result (queries/get-first-n-books 30)}})}}])

(def book-routes
  ["/book"
   {:swagger {:tags ["book"]}}

   [""
    {:post {:summary    "Insert a new book"
            :description "Example: {\"title\": \"test\", \"added\": \"1999-01-08T04:05:06\"}"
            :parameters {:body s/Any}                       ; todo: check that book is of correct type!
            ;:responses  {200 {:body s/Any}} ; is this correct like this?
            :handler    (fn [{{book :body} :parameters}]
                          (let [book (assoc book :added (LocalDateTime/parse (:added book)))
                                inserted-book (queries/create-book! book)]
                            {:status 200
                             :body   inserted-book}))}}]
   ["/:book-id"
    [""
     {:delete {:summary    "Given a books id, delete this book from the database"
               :parameters {:path {:book-id s/Str}}
               ;:responses  {200 {:body s/Any}} ; is this correct like this?
               :handler    (fn [{{{:keys [book-id]} :path} :parameters}]
                             (if (= 1 (queries/delete-book-by-id! (Integer/parseInt book-id)))
                               {:status 200
                                :body   {:id book-id}}
                               {:status 404
                                :body   {:id book-id}}))}}]
    #_["/front-matter"
     {:get {:summary    "Given a book id, returns a jpg picture of its front matter"
            :parameters {:path {:book-id s/Str}}
            :swagger    {:produces ["image/jpg"]}
            ; fixme: handle case where there is no image! (what is happening now, in that case?)
            :handler    (fn [{{{:keys [book-id]} :path} :parameters}]
                          {:status  200
                           :headers {"Content-Type" "image/png"}
                           :body    (when-let [image-url (:amazon-book-image-front (bookstore.db.model/get-book-by-id book-id))]
                                      (io/input-stream
                                        (io/resource (str (:front-matter-dir env) (get-file-name image-url)))))})}}]]])

(def book-management-routes
  [books-routes
   book-routes])
