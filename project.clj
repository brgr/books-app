(defproject amazon-wishlist "0.1.0-SNAPSHOT"
  :description "A small book CRUD app"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :source-paths ["src/main/clj"
                 "mongo/src/main/clj"]
  :test-paths ["src/test/clj"]
  :resource-paths ["src/main/resources"
                   "mongo/src/main/resources"
                   "public"]
  :dev {:resource-paths ["src/test/resources"]}

  :profiles {:dev [:project/dev :profiles/dev]
             :uberwar {:env {:database-url "database"}}}

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "1.0.0"]
                 [etaoin "0.3.10"]
                 [com.novemberain/monger "3.1.0"]
                 [metosin/reitit "0.5.13"]
                 [metosin/muuntaja "0.6.7"]
                 [org.jsoup/jsoup "1.13.1"]
                 [manifold "0.1.8"]
                 [ring-cors "0.1.13"]
                 [environ "1.1.0"]]

  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.1.0"]]

  :ring {:handler bookstore.api.routes/app}

  :repl-options {:init-ns amazon-wishlist.core}

  ; per default, we don't want to test amazon or integration, as these take too long
  :test-selectors {:default     (fn [m] (not (or (:amazon m) (:integration m))))
                   :amazon      :amazon
                   :integration :integration})
