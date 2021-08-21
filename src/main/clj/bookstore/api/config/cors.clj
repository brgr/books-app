(ns bookstore.api.config.cors)

(def allowed-headers
  "Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Referer, Origin, DNT, Connection, Pragma, Cache-Control")

(defn wrap-cors
  "Wrap the server response with new headers to allow Cross Origin."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "http://localhost:8280")
          (assoc-in [:headers "Access-Control-Allow-Headers"] allowed-headers)
          (assoc-in [:headers "Access-Control-Allow-Methods"] "*")))))
