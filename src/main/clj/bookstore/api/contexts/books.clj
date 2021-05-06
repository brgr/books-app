(ns bookstore.api.contexts.books
  (:require [bookstore.db.model :as bookstore]
            [clojure.spec.alpha :as spec]
            ;[schema.core :as s]
            [bookstore.db.update]
            ;[ring.util.http-response :refer [
                                             ;ok not-found
                                             ;header file-response]]
            [environ.core :refer [env]]
            [bookstore.files.file-management :refer [get-file-name]]))

(spec/def ::title string?)
(spec/def ::book-request (spec/keys :req-un [::title]))
(spec/def ::xy string?)
(spec/def ::book-response (spec/keys :req-un [::xy]))

(def books
  ["/books"
   {:swagger {:tags ["books"]}
    }

   [["/"
     ; todo: use a Schema to specify what is returned!
     {:get {:summary "Returns all books that are in the DB currently"
            :status  200
            ;:no-doc true
            :handler (fn [_] {:status 200
                              :body   (bookstore/all-books)})}}]

    ["/book"
     {:post {:summary    "Insert a new book"
             :parameters {:body ::book-request}
             :responses  {200 {:body ::book-response}}
             :handler    (fn [{{{:keys [title]} :body} :parameters}]
                           ; TODO: Now that this generally works, move the concrete call to here (see commented code below)
                           (println "title" title)
                           {:status 200
                            :body   {:xy title}})
             }}]

    ;(POST "/book" []
    ;  :summary "Insert a new book"
    ;  ; TODO: check that book is of the correct type! (schema check)
    ;  :body [book s/Any]
    ;  (let [id (bookstore/insert-new-book book)]
    ;    (ok id)))

    ]

   ;["/:book-id/front_matter"
   ; {:get {:summary ""
   ;        :handler (fn [{{{:keys [book-id]} :query} :parameters}]
   ;                   (when-let [image-url (:amazon-book-image-front (bookstore.db.model/get-book-by-id book-id))]
   ;                     (-> (get-file-name image-url)
   ;                         (file-response {:root (:front-matter-dir env)
   ;                                         :allow-symlinks? true})
   ;                         (header "Content-Type" "image/jpg"))))}}


   ]

  ; TODO: Migrate other calls to Reitit as well (just as the 1st call was done, above)

  ;(GET "/:book-id/front_matter" []
  ;  :summary "Fetch the thumbnail for the given book id"
  ;  :path-params [book-id :- String]
  ;  :produces ["image/jpg"]
  ;  ; fixme: handle case where there is no image! (what is happening now, in that case?)
  ;  (when-let [image-url (:amazon-book-image-front (bookstore.db.model/get-book-by-id book-id))]
  ;    (-> (get-file-name image-url)
  ;        (file-response {:root (:front-matter-dir env)
  ;                        :allow-symlinks? true})
  ;        (header "Content-Type" "image/jpg"))))

  ;

  ;
  ;(DELETE "/book" []
  ;  :summary "Delete book by given ID"
  ;  :query-params [id :- String]
  ;  (let [write-result (bookstore/remove-book-by-id id)
  ;        count-removed-books (.getN write-result)]
  ;    (if (= 1 count-removed-books)
  ;      (ok {:id id})
  ;      (not-found {:id id}))))
  ;
  ;(DELETE "/books/all" []
  ;  :summary "Delete all books in the database."
  ;  :description "This is currently not implemented in the frontend. You need to enter 'delete' as a parameter to go through with this."
  ;  :query-params [delete :- String]
  ;  (if (= delete "delete")
  ;    (let [result (bookstore/remove-all-books)]
  ;      (ok (str result)))))
  ;)
  )