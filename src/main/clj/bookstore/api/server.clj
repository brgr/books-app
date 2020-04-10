(ns bookstore.api.server
  (:require [compojure.api.sweet :refer :all]
            [schema.core :as s]
            [ring.util.http-response :refer :all]
            [bookstore.model :as bookstore]))

; to run local server on port 3000:
; lein ring server

;(def app
;  (api
;    (GET "/hello" []
;      :query-params [name :- String]
;      (ok {:message (str "Hello, " name)}))))

(s/defschema Pizza
  {:name                         s/Str
   (s/optional-key :description) s/Str
   :size                         (s/enum :L :M :S)
   :origin                       {:country (s/enum :FI :PO)
                                  :city    s/Str}})

(s/defschema Money
  {:amount                    s/Num
   (s/optional-key :currency) s/Str})

(s/defschema BookPrice
  {:price                 Money
   (s/optional-key :date) s/Str})

(s/defschema Book
  {:title                                          s/Str
   (s/optional-key :_id)                           s/Int
   (s/optional-key :authors)                       [s/Str]
   (s/optional-key :description)                   s/Str
   (s/optional-key :length)                        s/Int
   (s/optional-key :amazon-id)                     s/Int
   (s/optional-key :amazon-url)                    s/Str
   (s/optional-key :amazon-thumbnail-url)          s/Str
   (s/optional-key :amazon-date-added-to-wishlist) s/Str
   (s/optional-key :amazon-price)                  BookPrice})

(s/defschema Books [Book])


(def app
  (api
    {:swagger
     {:ui   "/api-docs"
      :spec "/swagger.json"
      :data {:info     {:title       "Books API"
                        :description "An API for retrieving and setting a (wish-)list of books"}
             :tags     ["api" "books" "wishlist"]
             :consumes ["application/json"]
             :produces ["application/json"]}}}

    (GET "/" []
      :summary "Redirects to /api-docs"
      (permanent-redirect "/api-docs"))

    (context "/books" []
      :tags ["books"]

      (GET "/" []
        ; todo: use a Schema to specify what is returned!
        :return s/Any
        :summary "returns all books that are in the DB currently"
        (ok
          {:result (bookstore/all-books)}
          ))

      (GET "/file" []
        :summary "file download"
        ;:return File
        :produces ["image/jpeg"]
        (->
            (file-response "img1.jpg" {:root "resources/public"})
            (header "Content-Type" "image/jpg"))))

    (context "/playground" []
      :tags ["playground"]

      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))

      (POST "/echo" []
        :return Pizza
        :body [pizza Pizza]
        :summary "echoes a Pizza"
        (ok pizza))

      (GET "/hello" []
        :query-params [name :- String]
        (ok {:message (str "Hello, " name)})))))