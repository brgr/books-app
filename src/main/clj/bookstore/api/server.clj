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
      :data {:info     {:title       "Sample API"
                        :description "Compojure Api example"}
             :tags     [{:name "api", :description "some apis"}]
             :consumes ["application/json"]
             :produces ["application/json"]}}}

    (context "/api" []
      :tags ["api"]

      (GET "/plus" []
        :return {:result Long}
        :query-params [x :- Long, y :- Long]
        :summary "adds two numbers together"
        (ok {:result (+ x y)}))

      (GET "/books" []
        :return s/Any                                       ; Books
        :summary "returns all books that are in the DB currently"
        (ok
          {:result bookstore/all-books}
          ; TODO: I think for the above to work, each book needs to be transformed into Book schema
          ))

      (POST "/echo" []
        :return Pizza
        :body [pizza Pizza]
        :summary "echoes a Pizza"
        (ok pizza))

      (GET "/hello" []
        :query-params [name :- String]
        (ok {:message (str "Hello, " name)}))

      (GET "/file" []
        :summary "file download"
        ;:return File
        :produces ["image/jpeg"]
        (->
            (file-response "img1.jpg" {:root "resources/public"})
            (header "Content-Type" "image/jpg"))))))