(ns bookstore.api.routes.import.amazon
  (:require
    [schema.core :as s]
    [bookstore.import.amazon :refer [import-wishlist books-loaded]]))

;; TODO: I have currently disabled this / This is currently unused.
;; 1. The main reason is that it's mostly unused and for a long time not tested.
;; 2. For that reason I have not yet migrated it to PostgreSQL. For that I would need to create a function (either a
;;    SQL function or simply a transaction) that makes it possible to insert multiple books at once.
;; 3. I would also need to rework wishlists. For example, I should create a table where I can save the wishlists that
;;    I have imported with some status (when was the last time I checked it, etc.).
;; 4. Finally, this is just not super important. What is important is that I am able to export my very own Amazon
;;    wishlist *once*. Then, I should just simply use the books app mostly. Since that is however happening while
;;    I'm still working on the books app as well, there is no real need to immediately have a fully-fledged wishlist
;;    import feature.

(def amazon-import-routes
  [["/import/amazon"
    {:swagger {:tags ["import"]}}

    ["/wishlist"
     {:put {:summary    "Import an amazon wishlist"
            :parameters {:query {:url s/Str}}
            :handler    (fn [{{{:keys [url]} :query} :parameters}]
                          {:status 200
                           :body   (-> (import-wishlist url)
                                       #_(insert-new-books))})}
      :get {:summary "Gets the current value of books loaded in the currently loaded wishlist, or 0 if no wishlist is currently loaded"
            :handler (fn [_]
                       {:status 200
                        :body @books-loaded})}}]]])
