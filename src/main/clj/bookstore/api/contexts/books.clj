(ns bookstore.api.contexts.books
  (:require [bookstore.db.model :as bookstore]
            [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok not-found header file-response]]
            [schema.core :as s]
            [bookstore.db.update]
            [environ.core :refer [env]]
            [bookstore.files.file-management :refer [get-file-name]]))

(def books
  (context "/books" []
    :tags ["books"]

    (GET "/" []
      ; todo: use a Schema to specify what is returned!
      :return s/Any
      :summary "returns all books that are in the DB currently"
      (ok {:result (bookstore/all-books)}))

    (GET "/:book-id/front_matter" []
      :summary "Fetch the thumbnail for the given book id"
      :path-params [book-id :- String]
      :produces ["image/jpg"]
      ; fixme: handle case where there is no image! (what is happening now, in that case?)
      (when-let [image-url (:amazon-book-image-front (bookstore.db.model/get-book-by-id book-id))]
        (-> (get-file-name image-url)
            (file-response {:root (:front-matter-dir env)
                            :allow-symlinks? true})
            (header "Content-Type" "image/jpg"))))

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
      :description "This is currently not implemented in the frontend. You need to enter 'delete' as a parameter to go through with this."
      :query-params [delete :- String]
      (if (= delete "delete")
        (let [result (bookstore/remove-all-books)]
          (ok (str result)))))))