(ns books.entrypoint
  (:require [reagent.dom :as dom]
            [re-frame.core :as reframe]
            [re-frame.core :refer [subscribe dispatch]]

            [books.views.views :as views]
            [books.views.base :as base-view]
            [books.routing :as routing]

            [reitit.frontend :as rf]
            [reitit.frontend.easy :as rfe]
            [reitit.coercion.spec :as rss]

    ; Note: the 2 below are needed s.t. they are loaded!!
            [books.events]
            [books.subs]))


(defn on-navigate [new-match]
  (when new-match
    (dispatch [:navigated new-match])))

(def router
  (rf/router routing/routes {:data {:coercion rss/coercion}}))

(defn router-component [{:keys [router]}]
  (let [current-route @(subscribe [:current-route])]
    [:div
     [base-view/nav {:router router :current-route current-route}]
     (when current-route
       [(-> current-route :data :view)])]))

(defn init-routes! []
  (rfe/start! router on-navigate {:use-fragment true}))

(defn render []
  (reframe/clear-subscription-cache!)
  (init-routes!)
  (dom/render [router-component {:router router}]
              (js/document.getElementById "books-app")))

(defn ^:dev/after-load clear-cache-and-render! []
  ;; The `:dev/after-load` metadata causes this function to be called
  ;; after shadow-cljs hot-reloads code. We force a UI update by clearing
  ;; the Reframe subscription cache.
  (reframe/clear-subscription-cache!)
  (render))

(defn run []
  (reframe/dispatch-sync [:initialize-books])
  ;; mount the application's ui into '<div id="app" />'
  (render))
