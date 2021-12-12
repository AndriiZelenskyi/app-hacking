(ns app-hacking.handlers.api
  (:use [app-hacking.db :as db]
        [clojure.data.json :only [read-str]]
        [org.httpkit.client :as http]))

(defn get-time [req]
  {:time (System/currentTimeMillis)
   :req  (merge req {:async-channel nil})})


(defn get-health [req]
  {:status (if (db/db-healthy?) 200 500)
   :body (if (db/db-healthy?) "UP" "DOWN")})


(defn c-test [req]
  {:response (:uuid (read-str (:body @(http/get "https://httpbin.org/uuid")) :key-fn keyword))})

(defn monobank [req]
  (let [url "https://api.monobank.ua/personal/client-info"
        ]
    {:response
     (:body
       (read-str
         (:body @(http/request {:url url, :headers headers :method :get}))))}))

(defn monobank-accounts [token]
  (let [account-endpoint "https://api.monobank.ua/personal/client-info"
        headers {"X-Token" token}]
    (->> @(org.httpkit.client/request {
                                       :url     account-endpoint
                                       :headers headers
                                       :method  :get})
         (:body)
         (#(read-str % :key-fn keyword))
         (:accounts)
         )))

(defn mono-headers [token] {"X-Token" token})

(defn monobank-transactions [token account-id]
  (let [from 1635724800
        to 1638230400
        transactions-endpoint (str "https://api.monobank.ua/personal/statement/" account-id "/" from "/" to)
        ]
    (->> @(org.httpkit.client/request {:url transactions-endpoint
                                       :headers (mono-headers token)
                                       :method :get})
         (:body)
         (#(read-str % :key-fn keyword))
         )))


(def primary-account-id "T8pMnBZVC7PbHv6FewTZTg")

(defn abs [n] (max n (- n)))

(defn sum-transactions [transactions]
  (->> (map :amount transactions)
       (reduce +)
       (abs)
       (* 0.01)
       (float)
       ))

(defn statistic [transactions]
  (->> (group-by :mcc transactions)
       (map (fn [[key transactions-in-category]]
              [key (sum-transactions transactions-in-category)]))
       (into {})))


(defn transactions-by-category [transactions] (group-by :mcc transactions))

(defn monobank-get-all-trasactions [token]
  (->> (monobank-accounts token)
       (map :id)
       (map #(list monobank-transactions token %))
       (take 2)
       (interpose (list #(Thread/sleep (* 61 1000))))
       (map #(apply (first %) (rest %)))
       ))
