(ns bookstore.api.config.cors)

(defn wrap-cors
  "Wrap the server response with new headers to allow Cross Origin."
  [handler]
  (fn [request]
    (let [response (handler request)]
      (-> response
          (assoc-in [:headers "Access-Control-Allow-Origin"] "http://localhost:8280")
          (assoc-in [:headers "Access-Control-Allow-Headers"]
                    "Host, User-Agent, Accept, Accept-Language, Accept-Encoding, Referer, Origin, DNT, Connection, Pragma, Cache-Control")
          (assoc-in [:headers "Access-Control-Allow-Methods"] "*")))))
