(ns books.entrypoint
  (:require [reagent.dom :as dom]
            [re-frame.core :as reframe]
            [books.views :as views]))

(defn render []
  (dom/render [views/single-book-edit]
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
  ;; put a value into application state
  (reframe/dispatch-sync [:initialize])
  ;; mount the application's ui into '<div id="app" />'
  (render))
