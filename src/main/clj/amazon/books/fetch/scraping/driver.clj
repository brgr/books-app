(ns amazon.books.fetch.scraping.driver
  (:require [etaoin.api :refer [chrome firefox-headless firefox go with-driver get-source quit get-logs]]
            [clojure.string :as str]))

; The useragents were originally taken from this site: (only the Browser user agents)
; https://techpatterns.com/downloads/firefox/useragentswitcher.xml
(def user-agents (-> (slurp "src/main/resources/useragents.txt")
                     (str/split #"\n")))

; Fixme: Move this to environ!
(def prefs-file-template "/home/dominik/.mozilla/firefox/hhib2u6i.default-release-1/prefs.js")
(def profile-dir "/home/dominik/.mozilla/firefox/djhmwgxs.useragent-switcher")
(def profile-prefs-file "/home/dominik/.mozilla/firefox/djhmwgxs.useragent-switcher/prefs.js")

(defn add-preference [preference value]
  (spit profile-prefs-file
        (str "\nuser_pref(\"" preference "\", \"" value "\");")
        :append true))

(defn remove-preference [preference]
  (as-> (slurp profile-prefs-file) $
        (clojure.string/split $ #"\n")
        (filter #(complement (str/includes? % preference)) $)
        (clojure.string/join $)))

(defn get-driver [headless?]
  ; Fixme: Save this as a template in my resources, then use that instead of the file from the Firefox that I currently use
  (->> (slurp prefs-file-template)
       (spit profile-prefs-file))
  (remove-preference "useragent")
  (add-preference "general.useragent.override" (rand-nth user-agents))
  (firefox {:headless     headless?
            :size         [1400 920]
            ; Note that we use another browser here (not the main installation of firefox)
            ; This other Firefox installation has the "useragent-switcher" profile set per default, which is then
            ; everytime updated
            ; Fixme: Probably put the following name also into environ
            :capabilities {:browserName "firefox-gecko"}}))
