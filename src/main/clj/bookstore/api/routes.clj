(ns bookstore.api.routes
  (:require
    [reitit.ring :as ring]
    [ring.adapter.jetty :as jetty]
    [bookstore.api.contexts.import.amazon :refer [amazon-import-routes]]
    [bookstore.api.contexts.books :refer [book-routes]]
    [bookstore.api.swagger :refer [swagger-json-route
                                   swagger-ui-handler]]
    [bookstore.api.reitit-options :refer [reitit-options]]))

(def routes
  [book-routes
   amazon-import-routes
   swagger-json-route])

(def default-404-handler
  (ring/create-default-handler
    {:not-found (constantly {:status 404 :body "Not found"})}))

(def app
  (ring/ring-handler
    (ring/router routes reitit-options)
    (ring/routes
      swagger-ui-handler
      default-404-handler)))


(comment
  (def server (jetty/run-jetty #'app {:port  3000
                                      :join? false}))

  (app {:request-method :post, :uri "/books/book"})

  )