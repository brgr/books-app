(ns bookstore.api.contexts.import.amazon
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok not-found header file-response]]
            [schema.core :as s]
            [environ.core :refer [env]]
            [manifold.deferred :as deferred]
            [bookstore.db.update]
            [amazon.books.parse.single-book :as single-book]))

(def amazon-import
  (context "/import/amazon" []
    :tags ["import"]

    (PUT "/wishlist" []
      :summary "Import a new amazon wishlist"
      ;:return String
      :query-params [url :- String]
      ; todo: start to fetch the wishlist!
      ;(let [id (bookstore/insert-new-wishlist-url url)]
      ;  (do
      ;    ;(Thread/sleep 10000)
      ;    (ok id)))
      (deferred/chain
        ;(http/get
        ;  (str "https://clojars.org/api/artifacts/" group "/" artifact)
        ;  {:as :json
        ;   :throw-exceptions false})
        ;:body
        ;:downloads
        ; todo: use this async call to fetch the wishlist - but first check if it works (on the frontend) with Thread/sleep
        (deferred/future (Thread/sleep 10000) (println "slept a little") "this is from wishlist")
        (ok)))

    (PUT "/book" []
      :summary "Fully import a book from its product page on Amazon"
      :return s/Any
      :query-params [book-id :- String]
      (deferred/chain
        (deferred/future
          (let [book (bookstore.db.model/get-book-by-id book-id)
                new-book-data (single-book/load-book (book :amazon-url))]
            (bookstore.db.update/update-book-from-amazon-product-page (book :_id) new-book-data)))
        ok))

    (GET "/downloads" []
      :summary "Async gets the clojars download count"
      ;:path-params [group :- s/Str, artifact :- s/Str]
      :return (s/maybe s/Int)
      (deferred/chain
        ;(http/get
        ;  (str "https://clojars.org/api/artifacts/" group "/" artifact)
        ;  {:as :json
        ;   :throw-exceptions false})
        ;:body
        ;:downloads
        ; todo: use this async call to fetch the wishlist - but first check if it works (on the frontend) with Thread/sleep
        (deferred/future (Thread/sleep 10000) (println "slept a little") 10)
        ok))))