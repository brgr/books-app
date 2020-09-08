(ns bookstore.api.server
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]
            [bookstore.db.update]
            [ring.middleware.cors :refer [wrap-cors]]
            [environ.core :refer [env]]
            [bookstore.api.contexts.books :refer [books]]
            [bookstore.api.contexts.import.amazon :refer [amazon-import]]))

; to run local server on port 3000:
; lein ring server

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

(def app
  (wrap-cors
    (api {:swagger swagger-options}
      root
      books
      amazon-import)

    ; the following are important for AJAX to work
    :access-control-allow-origin #"http://localhost:8280"
    :access-control-allow-headers ["Origin" "X-Requested-With"
                                   "Content-Type" "Accept"]
    :access-control-allow-methods [:get :put :post :delete :options]))