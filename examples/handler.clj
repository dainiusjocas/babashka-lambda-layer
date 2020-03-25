(ns handler)

(defn handle [event context]
  (println "hello from lambda")
  (assoc event "babashka" "rocks even harder"))
