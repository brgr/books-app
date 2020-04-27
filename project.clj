(defproject amazon-wishlist "0.1.0-SNAPSHOT"
  :description "FIXME: write description"                   ; todo
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :source-paths ["src/main/clj"]
  :test-paths ["src/test/clj"]
  :resource-paths ["src/main/resources"]
  :dev {:resource-paths ["src/test/resources"]}

  :profiles {:dev [:project/dev :profiles/dev]
             :uberwar {:env {:database-url "database"}}}

  :dependencies [[org.clojure/clojure "1.10.0"]
                 [etaoin "0.3.5"]
                 [com.novemberain/monger "3.1.0"]
                 [metosin/compojure-api "1.1.13"]
                 [org.jsoup/jsoup "1.13.1"]
                 [ring-cors "0.1.13"]
                 [environ "1.1.0"]]

  :plugins [[lein-ring "0.12.5"]
            [lein-environ "1.1.0"]]
  :ring {:handler bookstore.api.server/app}

  :repl-options {:init-ns amazon-wishlist.core}

  :test-selectors {:default     (fn [m] (not (or (:amazon m) (:integration m))))
                   :amazon      :amazon
                   :integration :integration})
