(ns bookstore.handler
  (:require
    [mount.core :as mount]
    [bookstore.middleware :as middleware]
    ;[bookstore.routes.home :refer [home-routes]]
    [bookstore.layout :refer [error-page]]
    ;[bookstore.api.routes :refer [home-routes-router]]
    [bookstore.api.routes :refer [home-routes]]
    [reitit.ring :as ring]
    [ring.middleware.content-type :refer [wrap-content-type]]
    [ring.middleware.webjars :refer [wrap-webjars]]
    [bookstore.env :refer [defaults]]))

(mount/defstate init-app
  :start ((or (:init defaults) (fn [])))
  :stop  ((or (:stop defaults) (fn []))))

(mount/defstate app-routes
  :start
  home-routes
  #_(ring/ring-handler
    home-routes-router
    ;(ring/router
    ;  [(home-routes)]
      ;routes
      ;)
    (ring/routes
      (ring/create-resource-handler
        {:path "/"})
      (wrap-content-type
        (wrap-webjars (constantly nil)))
      (ring/create-default-handler
        {:not-found
         (constantly (error-page {:status 404, :title "404 - Page not found"}))
         :method-not-allowed
         (constantly (error-page {:status 405, :title "405 - Not allowed"}))
         :not-acceptable
         (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

(defn app []
  (middleware/wrap-base #'app-routes))