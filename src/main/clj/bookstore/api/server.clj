(ns bookstore.api.server
  (:require [compojure.api.sweet :refer [api GET]]
            [ring.util.http-response :refer [permanent-redirect]]
            [ring.middleware.cors :refer [wrap-cors]]
            [bookstore.api.contexts.books :refer [books]]
            [bookstore.api.contexts.import.amazon :refer [amazon-import]]))

(def root
  (GET "/" []
    :summary "Redirects to /api-docs"
    (permanent-redirect "/api-docs")))

(def swagger-options
  {:ui   "/api-docs"
   :spec "/swagger.json"
   :data {:info     {:title       "Books API"
                     :description "An API for retrieving and setting a (wish-)list of books"}
          :tags     ["api" "books" "wishlist"]
          :consumes ["application/json"]
          :produces ["application/json"]}})

(def routes
  (api {:swagger swagger-options}
       root
       books
       amazon-import))

(def app
  (->
    routes
    (wrap-cors
     ; the following are important for AJAX to work
     :access-control-allow-origin #"http://localhost:8280"
     :access-control-allow-headers ["Origin" "X-Requested-With"
                                    "Content-Type" "Accept"]
     :access-control-allow-methods [:get :put :post :delete :options])))