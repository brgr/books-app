(ns bookstore.env
  (:require
    [clojure.tools.logging :as log]
    [bookstore.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[luminus-postgres-template started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[luminus-postgres-template has shut down successfully]=-"))
   :middleware wrap-dev})
