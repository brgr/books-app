(ns bookstore.api.reitit-options
  (:require
    [reitit.dev.pretty]
    [reitit.coercion.schema]
    [reitit.swagger :as swagger]
    [muuntaja.core :as m]
    [reitit.ring.middleware.muuntaja :as muuntaja]
    [reitit.ring.middleware.exception :as exception]
    [reitit.ring.middleware.multipart :as multipart]
    [reitit.ring.middleware.parameters :as parameters]
    [reitit.ring.coercion :as coercion]
    [ring.middleware.cors :refer [wrap-cors]]))

(def reitit-options
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
                             :access-control-allow-origin [#"http://localhost:8280"]
                             :access-control-allow-methods [:get :put :post :delete :options]]]}})