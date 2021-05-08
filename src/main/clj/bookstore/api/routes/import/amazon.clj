(ns bookstore.api.routes.import.amazon
  (:require
    [schema.core :as s]
    [bookstore.db.model :refer [insert-new-books]]
    [bookstore.import.amazon :refer [import-wishlist books-loaded]]))

; todo: also insert the wishlist itself into the wishlist DB
; todo: maybe immediately return how many books are going to be fetched, then fetch in the background?

(def amazon-import-routes
  [["/import/amazon"
    {:swagger {:tags ["import"]}}

    ["/wishlist"
     {:put {:summary    "Import an amazon wishlist"
            :parameters {:query {:url s/Str}}
            :handler    (fn [{{{:keys [url]} :query} :parameters}]
                          {:status 200
                           :body   (-> (import-wishlist url)
                                       (insert-new-books))})}
      :get {:summary "Gets the current value of books loaded in the currently loaded wishlist, or 0 if no wishlist is currently loaded"
            :handler (fn [_]
                       {:status 200
                        :body @books-loaded})}}]]])
