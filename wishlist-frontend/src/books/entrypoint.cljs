(ns books.entrypoint
  (:require [reagent.dom :as dom]
            [re-frame.core :as reframe]
            [books.views :as views]

            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]

            ; Note: the 2 below are needed s.t. they are loaded!!
            [books.events]
            [books.subs]))

(defn render []
  (rfe/start!
    (rf/router views/routes {:data {:coercion rss/coercion}})
    (fn [m] (reset! views/match m))
    ;; set to false to enable HistoryAPI
    {:use-fragment true})
  (dom/render [views/current-page]
              (js/document.getElementById "books-app")))

(defn ^:dev/after-load clear-cache-and-render! []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (reframe/clear-subscription-cache!)
  (render))

; TODO: I need to set this as :init-fn in project.clj
; I have currently not done that, as I still want to see results from the old project in this
; build.
(defn run []
  (reframe/dispatch-sync [:initialize-books])
  ;; mount the application's ui into '<div id="app" />'
  (render))
