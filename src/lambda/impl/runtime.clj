(ns lambda.impl.runtime
  (:require
    [cheshire.core :as json]
    [clj-http.lite.client :as http]))

(defn- get-lambda-invocation-request [runtime-api]
  (http/request
    {:method   :get
     :url      (str "http://" runtime-api "/2018-06-01/runtime/invocation/next")
     :timeout  900000}))

(defn- send-response [runtime-api lambda-runtime-aws-request-id response-body]
  (http/request
    {:method  :post
     :url     (str "http://" runtime-api "/2018-06-01/runtime/invocation/" lambda-runtime-aws-request-id "/response")
     :body    response-body
     :headers {"Content-Type" "application/json"}}))

(defn- send-error [runtime-api lambda-runtime-aws-request-id error-body]
  (http/request
    {:method  :post
     :url     (str "http://" runtime-api "/2018-06-01/runtime/invocation/" lambda-runtime-aws-request-id "/error")
     :body    error-body
     :headers {"Content-Type" "application/json"}}))

(defn- request->response [request-body handler-fn context]
  (let [decoded-request (json/decode request-body true)]
    (json/encode
      (if-let [body (:body decoded-request)]
        {:statusCode 200
         :body       (json/encode (handler-fn (json/decode body true) context))}
        (handler-fn decoded-request context)))))

(defn init [handler-fn context]
  (let [runtime-api (System/getenv "AWS_LAMBDA_RUNTIME_API")]
    (loop [req (get-lambda-invocation-request runtime-api)]
      (let [lambda-runtime-aws-request-id (get (get req :headers) "lambda-runtime-aws-request-id")]
        (when-let [error (get req :error)]
          (send-error runtime-api lambda-runtime-aws-request-id (str error))
          (throw (Exception. (str error))))
        (try
          (send-response runtime-api
                         lambda-runtime-aws-request-id
                         (request->response (get req :body) handler-fn context))
          (catch Exception e
            (send-error runtime-api lambda-runtime-aws-request-id (str e)))))
      (recur (get-lambda-invocation-request runtime-api)))))
