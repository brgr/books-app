(ns bookstore.api.routes
  (:require
    [reitit.ring :as ring]
    [reitit.dev.pretty]
    [reitit.coercion.schema]
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]
    [ring.adapter.jetty :as jetty]
    [bookstore.api.contexts.import.amazon :refer [amazon-import-routes]]
    [bookstore.api.contexts.books :refer [book-routes]]
    [bookstore.api.contexts.reitit-options :refer [reitit-options]]))

(def swagger-json
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:basePath "/"
                    :info     {:title       "Books API"
                               :description "API for managing meta-data on books"}}
          :handler (swagger/create-swagger-handler)}}])

(def routes
  [book-routes
   amazon-import-routes
   swagger-json])

(def app
  (ring/ring-handler
    (ring/router routes reitit-options)
    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/"
         :config {:validatorUrl     nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Not found"})}))))

(comment
  (def server (jetty/run-jetty #'app {:port  3000
                                      :join? false}))

  (app {:request-method :post, :uri "/books/book"})

  )