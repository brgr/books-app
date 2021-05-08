(ns bookstore.api.server-reitit
  (:require
    [reitit.ring :as ring]
    [reitit.dev.pretty]
    [reitit.coercion.schema]
    [reitit.swagger :as swagger]
    [ring.middleware.cors :refer [wrap-cors]]
    [reitit.swagger-ui :as swagger-ui]
    [muuntaja.core :as m]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.coercion :as coercion]
    [ring.adapter.jetty :as jetty]

    [bookstore.api.contexts.books :as book-context]))

(def swagger-json
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:basePath "/"
                    :info     {:title       "Books API"
                               :description "API for managing meta-data on books"}}
          :handler (swagger/create-swagger-handler)}}])

(def app
  (ring/ring-handler
    (ring/router
      [book-context/book-routes
       swagger-json]

      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception reitit.dev.pretty/exception
       :data      {:coercion   reitit.coercion.schema/coercion
                   :muuntaja   m/instance
                   :middleware [;; swagger feature
                                swagger/swagger-feature
                                ;; query-params & form-params
                                parameters/parameters-middleware
                                ;; content-negotiation
                                muuntaja/format-negotiate-middleware
                                ;; encoding response body
                                muuntaja/format-response-middleware
                                ;; exception handling
                                exception/exception-middleware
                                ;; decoding request body
                                muuntaja/format-request-middleware
                                ;; coercing response bodys
                                coercion/coerce-response-middleware
                                ;; coercing request parameters
                                coercion/coerce-request-middleware
                                ;; multipart
                                multipart/multipart-middleware
                                [wrap-cors
                                 ; Note: The whole problem was because the following string is a regex - not a string!
                                 :access-control-allow-origin [#"http://localhost:8280"]
                                 :access-control-allow-methods [:get :put :post :delete :options]]]}})

    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path   "/"
         :config {:validatorUrl     nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler
        ;{:not-found (constantly {:status 404 :body "Not found"})}
        ))))

(comment
  (def server (jetty/run-jetty #'app {:port  3000
                                      :join? false}))

  (app {:request-method :post, :uri "/books/book"})

  )