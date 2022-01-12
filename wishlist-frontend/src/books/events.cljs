(ns books.events
  (:require [ajax.core :refer [GET PUT POST DELETE json-request-format raw-response-format]]
            [re-frame.core :refer [reg-event-db reg-event-fx dispatch reg-fx]]
            [reitit.frontend.controllers :as rfc]
            [books.app :refer [BACKEND-URI]]))

(reg-event-db
  :initialize-books
  (fn [_ _]
    (let [_ (GET (str BACKEND-URI "/books")
                 {:response-format :json
                  :keywords?       true
                  :handler         (fn [response]
                                     (dispatch [:process-all-books (response :result)]))
                  ; TODO: error handler
                  :error-handler   #(do (println "error:" %1))})]
      {:single-book             {:book-title "Buchtitel"}
       :current-book-id         nil
       :all-books               []
       :current-amazon-wishlist {:url ""}
       :current-route           nil})))

(reg-event-fx
  :navigate
  (fn [db [_ & route]]
    ;; See `navigate` effect in routes.cljs
    {:navigate! route}))

(reg-event-db
  :navigated
  (fn [db [_ new-match]]
    (let [old-match (:current-route db)
          controllers (rfc/apply-controllers (:controllers old-match) new-match)]
      (assoc db :current-route (assoc new-match :controllers controllers)))))

(reg-event-db
  :insert-book-to-db
  (fn [db [_ new-book]]
    (do
      ; TODO: afterwards initiate a fetch to get all books anew
      (POST (str BACKEND-URI "/books/book") {:params        new-book
                                             :format        :json
                                             :handler       #(println "Worked fine:" %1)
                                             :error-handler #(println "Error:" %1)})
      db)))

(reg-event-db
  :remove-book-by-id
  (fn [db [_ book-to-remove]]
    (do
      (DELETE (str BACKEND-URI "/books/book") {:url-params      {:id (book-to-remove :book_id)}
                                               :response-format :json
                                               :handler         #(println "Worked fine:" %1)
                                               :error-handler   #(println "Error:" %1)})
      db)))

(reg-event-db
  :add-new-wishlist-url
  (fn [db [_ wishlist-url]]
    (do (PUT (str BACKEND-URI "/import/amazon/wishlist") {:url-params    {:url wishlist-url}
                                                          :handler       #(println "Worked fine:" %1)
                                                          :error-handler #(println "Error:" %1)})
        db)))

(reg-event-db
  :process-all-books
  (fn [db [_ all-books]]
    (println "processing books:" all-books)
    (assoc db :all-books all-books)))

(reg-event-db
  :process-search
  (fn [db [_ search-results]]
    (println "search results:" search-results)
    (assoc db :books-search-results search-results)))

(reg-event-db
  :edit-single-book
  (fn [db [_ new-single-book]]
    (assoc db :single-book new-single-book)))

(reg-event-db
  :update-current-book-id
  (fn [db [_ new-current-book-id]]
    (assoc db :current-book-id new-current-book-id)))


(reg-event-db
  :trigger-search
  (fn [db [_ search-text]]
    (println "sent:" (str BACKEND-URI "/search/amazon?search-text=" search-text))
    (GET (str BACKEND-URI "/search/amazon?search-text=" search-text)
         {:response-format :json
          :keywords?       true
          :handler         (fn [response]
                             (println "received response:" (:result response))
                             (dispatch [:process-search (:result response)]))
          ; TODO: error handler
          :error-handler   #(do (println "error:" %1))})
    (assoc db :loading-search-results? true)))

(reg-event-db
  :update-current-search
  (fn [db [_ new-current-search]]
    (assoc db :current-search new-current-search)))

(reg-event-db
  :update-in-single-book
  (fn [db [_ path new-value]]
    (assoc-in db [:single-book (first path)] new-value)))

(reg-event-db
  :update-in-amazon-wishlist
  (fn [db [_ path new-value]]
    (assoc-in db [:current-amazon-wishlist (first path)] new-value)))
