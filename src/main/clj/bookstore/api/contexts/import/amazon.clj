(ns bookstore.api.contexts.import.amazon
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer [ok not-found header file-response]]
            [schema.core :as s]
            [environ.core :refer [env]]
            [bookstore.db.model :refer [insert-new-books]]
            [bookstore.import.amazon :refer [import-wishlist books-loaded]]))

(def amazon-import
  (context "/import/amazon" []
    :tags ["import"]

    (PUT "/wishlist" []
      :summary "Import a new amazon wishlist"
      :return s/Any
      :query-params [url :- String]

      ; todo: also insert the wishlist itself into the wishlist DB
      ; todo: maybe immediately return how many books are going to be fetched, then fetch in the background?
      (-> (import-wishlist url)
          (insert-new-books))
      (ok))

    (GET "/wishlist" []
      :summary "Gets the current value of books loaded in the currently loaded wishlist, or 0 if no wishlist is
      currently loaded."
      :return s/Any
      (ok @books-loaded))))