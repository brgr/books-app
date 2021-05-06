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
      {;;:reitit.middleware/transform dev/print-request-diffs ;; pretty diffs
       ;;:validate spec/validate ;; enable spec validation for route data
       ;;:reitit.spec/wrap spell/closed ;; strict top-level validation
       :exception reitit.dev.pretty/exception
       :data      {:coercion   reitit.coercion.spec/coercion
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
                                 ;:access-control-allow-headers ["*"
                                                                ;"Accept"
                                                                ;"Accept-Encoding"
                                                                ;"Accept-Language"
                                                                ;"Cache-Control"
                                                                ;"Content-Length"
                                                                ;"Connection"
                                                                ;"Origin"
                                                                ;"X-Requested-With"
                                                                ;"Content-Type"
                                                                ;"User-Agent"
                                                                ;"Host"
                                                                ;"Referer"
                                                                ;"DNT"
                                                                ;"Cookie"
                                                                ;"Pragma"
                                                                ;]
                                 :access-control-allow-methods [:get :put :post :delete :options]]]}}
      )
    
;    "POST /book HTTP/1.1
;Host: localhost:3000
;User-Agent: Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:87.0) Gecko/20100101 Firefox/87.0
;Accept: application/json
;Accept-Language: en-US,en;q=0.5
;Accept-Encoding: gzip, deflate
;Referer: http://localhost:3000/index.html
;Content-Type: application/json
;Origin: http://localhost:3000
;Content-Length: 23
;DNT: 1
;Connection: keep-alive
;Cookie: JSESSIONID=E7EW8PFON8EAAV7aliAtFuvnKgi9XCeZ8UIkw7it
;Pragma: no-cache
;Cache-Control: no-cache"

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