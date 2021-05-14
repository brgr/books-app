(ns bookstore.api.routes
  (:require
    [reitit.ring :as ring]
    [ring.middleware.cors :as cors]
    ;[ring.middleware.defaults :refer [site-defaults wrap-defaults]]
    [bookstore.api.routes.import.amazon :refer [amazon-import-routes]]
    [bookstore.api.routes.search.amazon :refer [amazon-search-routes]]
    [bookstore.api.routes.books :refer [book-management-routes]]
    [bookstore.api.swagger :refer [swagger-json-route
                                   swagger-ui-handler]]
    [bookstore.api.reitit-options :refer [reitit-options]]))

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

(defn wrap-cors
  "Wrap the server response with new headers to allow Cross Origin."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "http://localhost:8280")
          (assoc-in [:headers "Access-Control-Allow-Headers"]
                    "Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Referer, Origin, DNT, Connection, Pragma, Cache-Control")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "*")))))

(defn wrap-base [handler]
  (->
    ;((:middleware defaults) handler)
    handler
    (wrap-cors)
    ;(wrap-session {:cookie-attrs {:http-only true}})
    ;(wrap-defaults
    ;  (-> site-defaults
    ;      (assoc-in [:security :anti-forgery] false)
    ;      (dissoc :session)))
    ))

(def app
  (wrap-base app-routes))