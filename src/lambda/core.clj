(ns lambda.core
  (:require
    [clojure.string :as str]
    [lambda.impl.runtime :as runtime]))

; https://docs.aws.amazon.com/lambda/latest/dg/configuration-envvars.html
(defn resolve-handler-fn [handler code-dir]
  (let [[namespace fn-name] (str/split handler #"/")
        file-path (str code-dir
                       "/"
                       (-> namespace
                           (str/replace "." "/")
                           (str/replace "-" "_")
                           (str ".clj")))
        _ (load-file file-path)]
    (get (ns-publics (symbol namespace)) (symbol fn-name))))

(defn -main [& _]
  (let [handler (or (System/getenv "_HANDLER") "handler/handle")
        code-dir (or (System/getenv "LAMBDA_TASK_ROOT") "./")
        context (System/getenv)
        handler-fn (resolve-handler-fn handler code-dir)]
    (if handler-fn
      (runtime/init handler-fn context)
      (throw (Exception. (format "Lambda handler '%s' not found" handler))))))
