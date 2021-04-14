(ns bookstore.api.server-reitit
  (:require [environ.core :as env]
            [reitit.ring :as ring]
            [reitit.coercion.schema]
            [reitit.swagger :as swagger]
            [ring.middleware.cors :refer [wrap-cors]]
            [reitit.swagger-ui :as swagger-ui]
            [schema.core]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as coercion]

            [bookstore.api.contexts.books :refer [books]]
            ))


(defn ping-handler [_]
  {:status  200
   :body    "ok"
   :no-doc  true
   :swagger {:tags ["files"]}})

(def app
  (ring/ring-handler
    (ring/router
      [["/ping" {:get ping-handler}]

       books

       ["/swagger.json"
        {:get {:no-doc  true
               :swagger {:basePath "/"
                         :info     {:title       "Books API"
                                    :description "API for managing meta-data on books"}}
               :handler (swagger/create-swagger-handler)}}]]

      ; ["/math"
      ;  {:swagger {:tags ["math"]}}
      ;
      ;  ;["/plus"
      ;  ; {:get  {:summary    "plus with spec query parameters"
      ;  ;         :parameters {:query {:x int?, :y int?}}
      ;  ;         :responses  {200 {:body {:total int?}}}
      ;  ;         :handler    (fn [{{{:keys [x y]} :query} :parameters}]
      ;  ;                       {:status 200
      ;  ;                        :body   {:total (+ x y)}})}
      ;  ;  :post {:summary    "plus with spec body parameters"
      ;  ;         :parameters {:body {:x int?, :y int?}}
      ;  ;         :responses  {200 {:body {:total int?}}}
      ;  ;         :handler    (fn [{{{:keys [x y]} :body} :parameters}]
      ;  ;                       {:status 200
      ;  ;                        :body   {:total (+ x y)}})}}]]]
      ;
      {:data {:coercion   reitit.coercion.schema/coercion
              :muuntaja   m/instance
              ; todo: I am not sure which of these are actually needed... look them up, maybe I can remove some
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware
                           exception/exception-middleware
                           muuntaja/format-request-middleware
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware
                           multipart/multipart-middleware
                           [wrap-cors
                            :access-control-allow-origin ["http://localhost:8280"]
                            :access-control-allow-headers ["Origin" "X-Requested-With"
                                                           "Content-Type" "Accept"]
                            :access-control-allow-methods [:get :put :post :delete :options]]]}})

    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"})
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Not found"})}))))
