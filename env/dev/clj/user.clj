(ns user
  "Userspace functions you can run by default in your local REPL."
  (:require
    [bookstore.config :refer [env]]
    [bookstore.db.core]
    [bookstore.core]
    [clojure.pprint]
    [clojure.spec.alpha :as s]
    [expound.alpha :as expound]
    [mount.core :as mount]
    [conman.core :as conman]
    [luminus-migrations.core :as migrations]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(add-tap (bound-fn* clojure.pprint/pprint))

(defn restart-db
  "Restarts database."
  []
  (mount/stop #'bookstore.db.core/*db*)
  (mount/start #'bookstore.db.core/*db*)
  (binding [*ns* (the-ns 'bookstore.db.core)]
    (conman/bind-connection bookstore.db.core/*db* "sql/queries.sql")))

(defn start
  "Starts application.
  You'll usually want to run this on startup."
  []
  (mount/start-without #'bookstore.core/repl-server))

(defn stop
  "Stops application."
  []
  (mount/stop-except #'bookstore.core/repl-server))

(defn restart
  "Restarts application."
  []
  (stop)
  (start))

(defn reset-db
  "Resets database."
  []
  (migrations/migrate ["reset"] (select-keys env [:database-url])))

(defn migrate
  "Migrates database up for all outstanding migrations."
  []
  (migrations/migrate ["migrate"] (select-keys env [:database-url])))

(comment
  (migrate)
  (restart-db)
  )

(defn rollback
  "Rollback latest database migration."
  []
  (migrations/migrate ["rollback"] (select-keys env [:database-url])))

(defn create-migration
  "Create a new up and down migration file with a generated timestamp and `name`."
  [name]
  (migrations/create name (select-keys env [:database-url])))
