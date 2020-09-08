(ns bookstore.api.contexts.books
  (:require [bookstore.db.model :as bookstore]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok not-found header file-response]]
            [schema.core :as s]
            [bookstore.db.update]
            [environ.core :refer [env]]))

(def books
  (context "/books" []
    :tags ["books"]

    (GET "/" []
      ; todo: use a Schema to specify what is returned!
      :return s/Any
      :summary "returns all books that are in the DB currently"
      (ok {:result (bookstore/all-books)}))

    (GET "/:book-id/thumbnail" []
      :summary "Fetch the thumbnail for the given book id"
      :path-params [book-id :- String]
      :produces ["image/jpg"]
      (->
        (bookstore.db.model/get-book-by-id book-id)
        :thumbnail
        (file-response {:root (env :thumbnails-dir)})
        (header "Content-Type" "image/jpg")))

    (POST "/book" []
      :summary "Insert a new book"
      ; TODO: check that book is of the correct type! (schema check)
      :body [book s/Any]
      (let [id (bookstore/insert-new-book book)]
        (ok id)))

    (DELETE "/book" []
      :summary "Delete book by given ID"
      :query-params [id :- String]
      (let [write-result (bookstore/remove-book-by-id id)
            count-removed-books (.getN write-result)]
        (if (= 1 count-removed-books)
          (ok {:id id})
          (not-found {:id id}))))

    (DELETE "/books/all" []
      :summary "Delete all books in the database."
      :description "This is currently not implemented in the frontend. You need to enter\n      'delete' as a parameter to go through with this."
      :query-params [delete :- String]
      (if (= delete "delete")
        (let [result (bookstore/remove-all-books)]
          (ok (str result)))))

    ; TODO: Next steps:
    ; 1. add an API method to fetch the thumbnail given a book id
    ; 2. add a book view to the frontend, including the thumbnail
    ; 3. maybe switch from thumbnail to main picture (this needs fetching of the main site of the book from amazon)
    (PUT "/book/load_thumbnail" []
      :return s/Any
      :query-params [book-id :- String]
      :summary "Load the thumbnail from the URL that is saved for this book's thumbnail."
      (ok {:result (bookstore.db.update/load-book-thumbnail book-id)}))))