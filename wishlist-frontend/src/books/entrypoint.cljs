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

    ; Note: these below are needed s.t. they are loaded with the entrypoint
            [books.events]
            [books.effects]
            [books.subs]))


(defn on-navigate [new-match]
  (when new-match
    (dispatch [:navigated new-match])))

(def router
  (rf/router
    routing/routes
    {:data {:controllers [{:start (js/console.log "start" "root-controller")
                           :stop  (js/console.log "stop" "root controller")}]
            :coercion    rss/coercion}}))

(defn router-component [{:keys [router]}]
  (let [current-route @(subscribe [:current-route])]
    [:div
     [:div.top-bar
      [base-view/nav {:router router :current-route current-route}]
      [:div.search-outer-container
       [:div.search-inner-container
        [:input.searchTerm {:type "text"
                            :placeholder "What are you looking for?"}]
        [:button.searchButton {:type "submit"}
         [:i.fa.fa-search]]]]]
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
