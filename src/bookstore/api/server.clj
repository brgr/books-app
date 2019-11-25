(ns bookstore.api.server
  (:require [compojure.api.sweet :refer :all]
           [ring.util.http-response :refer :all]))

; to run local server on port 3000:
; lein ring server

(def app
  (api
    (GET "/hello" []
      :query-params [name :- String]
      (ok {:message (str "Hello, " name)}))))