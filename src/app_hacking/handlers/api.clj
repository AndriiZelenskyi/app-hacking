(ns app-hacking.handlers.api
  (:use [app-hacking.db :as db]))

(defn get-time [req]
  {:time (System/currentTimeMillis)
   :req (merge req {:async-channel nil})})

(defn get-health [req]
  {:status (if (db-healthy?) 200 500)
   :body (if (db-healthy?) "UP" "DOWN")})
