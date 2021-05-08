(ns bookstore.api.swagger
  (:require
    [reitit.swagger :as swagger]
    [reitit.swagger-ui :as swagger-ui]))

(def swagger-json-route
  ["/swagger.json"
   {:get {:no-doc  true
          :swagger {:basePath "/"
                    :info     {:title       "Books API"
                               :description "API for managing meta-data on books"}}
          :handler (swagger/create-swagger-handler)}}])

(def swagger-ui-handler
  (swagger-ui/create-swagger-ui-handler
    {:path   "/"
     :config {:validatorUrl     nil
              :operationsSorter "alpha"}}))
