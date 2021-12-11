(ns app-hacking.handlers.dao
  (:use [app-hacking.db :as db]))

(defn save-transaction [{:keys [ amount comment mcc]}]
  (db/insert :mono_transaction [{:amount amount :comment comment :mcc mcc}]))

(defn save-account [{:keys [ mono-id account-id ]}]
  (db/insert :mono_account [{:mono_id mono-id :account_id account-id}]))

(defn list-transactions [req]
  (db/list-table :mono_transaction))

(defn list-accounts [req]
  (db/list-table :mono_account))

; Populate DB
(save-transaction {:amount 100 :comment "ROFL" :mcc 12})
(save-account {:account-id 12 :mono-id 120})