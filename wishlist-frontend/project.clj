(defproject hello-reagent-reframe "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}

  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.597"
                  :exclusions [com.google.javascript/closure-compiler-unshaded
                               org.clojure/google-closure-library
                               org.clojure/google-closure-library-third-party]]
                 [thheller/shadow-cljs "2.8.83"]
                 [reagent "0.10.0"]
                 [re-frame "0.12.0"]

                 [binaryage/devtools         "0.9.10"]
                 [clj-commons/secretary      "1.2.4"]
                 [day8.re-frame/tracing      "0.5.3"]]

  :plugins [[lein-shadow "0.1.7"]]

  :clean-targets ^{:protect false} [:target-path
                                    "shadow-cljs.edn"
                                    "package.json"
                                    "package-lock.json"
                                    "resources/public/js"]

  :shadow-cljs {:nrepl  {:port 8777}

                :builds {:client {:target     :browser
                                  :output-dir "resources/public/js"
                                  :modules    {:client {:init-fn hello-reagent-reframe.core/run}}
                                  :devtools   {:http-root "resources/public"
                                               :http-port 8280}}}}

  :aliases {"dev-auto" ["shadow" "watch" "client"]}

  :repl-options {:init-ns hello-reagent-reframe.core})
