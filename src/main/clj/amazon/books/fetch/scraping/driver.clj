(ns amazon.books.fetch.scraping.driver
  (:require
    [etaoin.api :refer [chrome firefox-headless firefox go with-driver get-source quit get-logs]]
    [etaoin.api :refer :all]
    [clojure.string :as str]
    [bookstore.config :refer [env]]))

; The useragents were originally taken from this site: (only the Browser user agents)
; https://techpatterns.com/downloads/firefox/useragentswitcher.xml
(def user-agents (-> (slurp (env :user-agents-file))
                     (str/split #"\n")))

(def prefs-file-template (env :firefox-profile-prefs-template-file))
; fixme: make this more dynamic, see issue #6
(def profile-prefs-file (str (env :firefox-profile-directory) "prefs.js"))

(defn add-preference [preference value]
  (spit profile-prefs-file
        (str "\nuser_pref(\"" preference "\", \"" value "\");")
        :append true))

(defn remove-preference [preference]
  (as-> (slurp profile-prefs-file) $
        (clojure.string/split $ #"\n")
        (filter #(complement (str/includes? % preference)) $)
        (clojure.string/join $)))

(defn get-driver []
  (->> (slurp prefs-file-template)
       (spit profile-prefs-file))
  (remove-preference "useragent")
  (add-preference "general.useragent.override" (rand-nth user-agents))
  (firefox {:headless     (= "true" (env :headless-scraping))
            :size         [1400 920]
            ; Note that we use another browser here (not the main installation of firefox)
            ; This other Firefox installation has the "useragent-switcher" profile set per default, which is then
            ; everytime updated
            :capabilities {:browserName (env :firefox-browser-name)}}))

(defn get-generic-html [url]
  (let [driver (get-driver)]
    (try
      (with-wait-timeout 30
        (go driver url)
        (get-source driver))
      (finally
        (quit driver)))))
