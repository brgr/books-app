(defproject amazon-wishlist "0.1.0-SNAPSHOT"
  :description "A small book CRUD app"
  :url "http://example.com/FIXME"

  :source-paths ["src/main/clj"]
  :test-paths ["src/test/clj"]
  :resource-paths ["src/main/resources"
                   "env/shared/resources"]

  :profiles {:uberwar      {:env {:database-url "database"}}

             :dev          [:project/dev :profiles/dev]

             :project/dev  {:jvm-opts       ["-Dconf=dev-config.edn"]
                            :dependencies   [[pjstadig/humane-test-output "0.11.0"]
                                             [prone "2021-04-23"]
                                             [ring/ring-devel "1.9.4"]]
                            :plugins        [[com.jakemccrary/lein-test-refresh "0.24.1"]
                                             [jonase/eastwood "0.3.5"]]

                            :source-paths   ["env/dev/clj"]
                            :resource-paths ["env/dev/resources"
                                             "src/test/resources"]
                            :repl-options   {:init-ns user
                                             :timeout 120000}
                            :injections     [(require 'pjstadig.humane-test-output)
                                             (pjstadig.humane-test-output/activate!)]}

             :profiles/dev {}}


  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.json "1.0.0"]
                 [etaoin "0.3.10"]
                 [nrepl "0.8.3"]
                 [ch.qos.logback/logback-classic "1.2.5"]
                 [org.clojure/tools.logging "1.1.0"]
                 [org.clojure/tools.cli "1.0.206"]
                 [org.postgresql/postgresql "42.2.18"]
                 [luminus-transit "0.1.3"]
                 [luminus-undertow "0.1.14"]
                 [luminus/ring-ttl-session "0.3.3"]
                 [metosin/ring-http-response "0.9.3"]
                 [ring-webjars "0.2.0"]
                 [ring/ring-core "1.9.4"]
                 [ring/ring-defaults "0.3.3"]
                 [selmer "1.12.49"]
                 [markdown-clj "1.10.8"]
                 [luminus-migrations "0.7.1"]
                 [mount "0.1.16"]
                 [cprop "0.1.17"]
                 [conman "0.9.1"]
                 [metosin/reitit "0.5.13"]
                 [metosin/muuntaja "0.6.7"]
                 [org.jsoup/jsoup "1.13.1"]
                 [manifold "0.1.8"]
                 [ring-cors "0.1.13"]
                 [hashp "0.2.1"]
                 [camel-snake-kebab "0.4.2"]]

  :plugins [[lein-ring "0.12.5"]]

  :injections [(require 'hashp.core)]

  :ring {:handler bookstore.api.routes/app}
  :main ^:skip-aot bookstore.core

  :repl-options {:init-ns amazon-wishlist.core}

  ; per default, we don't want to test amazon or integration, as these take too long
  :test-selectors {:default     (fn [m] (not (or (:amazon m) (:integration m))))
                   :amazon      :amazon
                   :integration :integration})
