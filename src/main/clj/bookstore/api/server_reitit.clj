(ns bookstore.api.server-reitit
  (:require [environ.core :as env]
            [reitit.ring :as ring]
            [reitit.spec]
            [reitit.dev.pretty]
            [reitit.coercion.spec]
            [reitit.swagger :as swagger]
            [ring.middleware.cors :refer [wrap-cors]]
            [reitit.swagger-ui :as swagger-ui]
            ;[schema.core]
            [clojure.spec.alpha :as spec]
            [muuntaja.core :as m]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.multipart :as multipart]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.coercion :as coercion]
            [ring.adapter.jetty :as jetty]

            [bookstore.api.contexts.books :refer [books]]
            ))


(defn ping-handler [_]
  {:status  200
   :body    "ok"
   :no-doc  true
   :swagger {:tags ["files"]}})

(spec/def ::title string?)
(spec/def ::book-request (spec/keys :req-un [::title]))
(spec/def ::xy string?)
(spec/def ::book-response (spec/keys :req-un [::xy]))

(def app
  (ring/ring-handler
    (ring/router
      [["/ping" {:get ping-handler}]

       ;books

       ["/swagger.json"
        {:get {:no-doc  true
               :swagger {:basePath "/"
                         :info     {:title       "Books API"
                                    :description "API for managing meta-data on books"}}
               :handler (swagger/create-swagger-handler)}}]


       ["/book"
        {:post {:summary    "Insert a new book"
                :parameters {:body ::book-request}
                :responses  {200 {:body ::book-response}}
                :handler    (fn [{{{:keys [title]} :body} :parameters}]
                              (println "title" title)
                              {:status 200
                               :body   {:xy title}})
                }}]
       ]

      ; TODO: Slowly move books + amazon-import API calls from using compojure-api to reitit
      ; Note that the calls below are just here for reference, they can be removed after migration

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
              ; todo: I am not sure which of these are actually needed... look them up, maybe I can remove some
              :middleware [parameters/parameters-middleware
                           muuntaja/format-negotiate-middleware
                           muuntaja/format-response-middleware
                           exception/exception-middleware
                           muuntaja/format-request-middleware
                           coercion/coerce-response-middleware
                           coercion/coerce-request-middleware
                           coercion/coerce-exceptions-middleware

                           multipart/multipart-middleware
                           [wrap-cors
                            :access-control-allow-origin ["http://localhost:8280"]
                            :access-control-allow-headers ["Origin" "X-Requested-With"
                                                           "Content-Type" "Accept"]
                            :access-control-allow-methods [:get :put :post :delete :options]]]}
       ;:validate reitit.spec/validate
       :exception reitit.dev.pretty/exception}
      )

    (ring/routes
      (swagger-ui/create-swagger-ui-handler
        {:path "/"
         :config {:validatorUrl nil
                  :operationsSorter "alpha"}})
      (ring/create-default-handler
        ;{:not-found (constantly {:status 404 :body "Not found"})}
        ))))

; TODO:
; Unfortunately, it doesn't work like this.
; My proposal: Create it completely anew, just slightly copying some stuff
; This time, try it out with Luminus:
; https://luminusweb.com/docs/services.html
; With that, I should be able to have it set up like here (for reitit) in 2-3 hours,
; hopefully. That will likely be less time than troubleshooting here.

(comment
  (def server (jetty/run-jetty #'app {:port  3000
                                      :join? false}))

  (app {:request-method :post, :uri "/books/book"})

  )