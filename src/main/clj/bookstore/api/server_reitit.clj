(ns bookstore.api.server-reitit
  (:require [environ.core :as env]
            [reitit.ring :as ring]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [ring.adapter.jetty :as jetty]
            [reitit.swagger-ui :as swagger-ui]
    ;[schema.core :as s]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as coercion]
            ))


(defn ping-handler [_]
  {:status  200
   :body    "ok"
   :no-doc  true
   :swagger {:tags ["files"]}})

(def app
  (ring/ring-handler
    (ring/router
      [["/ping" {:get    ping-handler
                 ;:no-doc true
                 }]

       ["/swagger.json"
        {:get {:no-doc  true
               :swagger {:basePath "/"
                         :info     {:title       "my-api"
                                    :description "with reitit-ring"}}
               :handler (swagger/create-swagger-handler)}}]]
      ;["/api-docs/*" {:get (swagger-ui/create-swagger-ui-handler)}]

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
      {:data {:coercion   reitit.coercion.spec/coercion
              :muuntaja   m/instance
              :middleware [;; query-params & form-params
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
                           multipart/multipart-middleware]}})

    ; FIXME:
    ; This generally works, but swagger-ui does not. Somehow, when starting, it directly redirects to /api-docs -
    ; even though this is never specifid in server_reitit!! I don't understand why it does this...
    ; Furthermore, the config options (also :root etc, not only :config ...) below somehow don't work as expected...

    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        ; fixme: why does it not work if the path is set to "/"
        {
         ;:root "/"
         :path "/"
         ;:basePath "/"
         ;:url       "/swagger.json"
         ;:config    {
         ;            :configUrl "/swagger.json"
         ;            :url              "/swagger.json"
         ;            :validatorUrl     nil
         ;            :operationsSorter nil}
         ;:responses {200 {:schema s/Any}}
         })
      (ring/create-default-handler
        {:not-found (constantly {:status 404 :body "Not found"})}))
    )
  )


;(defn -main []
  ;(migrate)
  ;(jetty/run-jetty #'app {:port  3000
  ;                        :join? false}))