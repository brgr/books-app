(ns bookstore.api.routes.books
  (:require
    [schema.core :as s]
    #_#_[bookstore.db.model :as bookstore]
    [bookstore.db.update]
    [bookstore.db.queries :as queries]
    [clojure.java.io :as io]
    [bookstore.files.file-management :refer [get-file-name]]))

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

   #_#_[""
    {:post {:summary    "Insert a new book"
            :parameters {:body s/Any}                       ; todo: check that book is of correct type!
            ;:responses  {200 {:body s/Any}} ; is this correct like this?
            :handler    (fn [{{book :body} :parameters}]
                          (let [inserted-book (bookstore/insert-new-book book)]
                            {:status 200
                             :body   inserted-book}))}}]
   ["/:book-id"
    [""
     {:delete {:summary    "Given a books id, delete this book from the database"
               :parameters {:path {:book-id s/Str}}
               ;:responses  {200 {:body s/Any}} ; is this correct like this?
               :handler    (fn [{{{:keys [book-id]} :path} :parameters}]
                             (let [write-result (bookstore/remove-book-by-id book-id)
                                   count-removed-books (.getN write-result)]
                               (if (= 1 count-removed-books)
                                 {:status 200
                                  :body   {:id book-id}}
                                 {:status 404
                                  :body   {:id book-id}})))}}]
    ["/front-matter"
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
