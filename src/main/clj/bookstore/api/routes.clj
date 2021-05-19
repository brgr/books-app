(ns bookstore.api.routes
  (:require
    [reitit.ring :as ring]
    [bookstore.api.routes.import.amazon :refer [amazon-import-routes]]
    [bookstore.api.routes.search.amazon :refer [amazon-search-routes]]
    [bookstore.api.routes.books :refer [book-management-routes]]
    [bookstore.api.swagger :refer [swagger-json-route
                                   swagger-ui-handler]]
    [bookstore.api.config.reitit-options :refer [reitit-options]]
    [bookstore.api.config.cors :refer [wrap-cors]]))

(def routes
  [book-management-routes
   amazon-import-routes
   amazon-search-routes
   swagger-json-route])

(def default-404-handler
  (ring/create-default-handler
    {:not-found (constantly {:status 404 :body "Not found"})}))

(def app-routes
  (ring/ring-handler
    (ring/router routes reitit-options)
    (ring/routes
      swagger-ui-handler
      default-404-handler)))

(def app
  (wrap-cors app-routes))