(ns bookstore.core
  (:require
    [bookstore.nrepl :as nrepl]
    ; todo: add the migrations at project start (have a look at luminus!)
    ;[luminus-migrations.core :as migrations]
    [bookstore.config :refer [env]]
    [clojure.tools.logging :as log]
    [mount.core :as mount])
  (:gen-class))

(mount/defstate ^{:on-reload :noop} repl-server
  :start
  (when (env :nrepl-port)
    (nrepl/start {:bind (env :nrepl-bind)
                  :port (env :nrepl-port)}))
  :stop
  (when repl-server
    (nrepl/stop repl-server)))