(ns app-hacking.db
  (:require [clojure.java.jdbc :as jdb]))

(def mysql-options {:dbtype   "mysql"
                    :dbname   "app-hacking"
                    :user     "root"
                    :password "root"})

(defn db-healthy? []
  (jdb/query mysql-options
             ["select 1 as res"]
             {:row-fn :res}))

(def transaction-table-ddl
  (jdb/create-table-ddl :mono_transaction [
                                           [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                                           [:amount "BIGINT"]
                                           [:comment "VARCHAR(255)"]
                                           [:mcc :int]
                                           [:mono_account_id :int]
                                           [:bank_integration_id :int]
                                           ["FOREIGN KEY (mono_account_id) REFERENCES mono_account (id) ON DELETE CASCADE"]
                                           ["FOREIGN KEY (bank_integration_id) REFERENCES integrated_banks (id) ON DELETE CASCADE"]]
                        {:conditional? true}))
(def monobank-accounts-table-ddl
  (jdb/create-table-ddl :mono_account [
                                       [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                                       [:mono_id :int]
                                       [:account_id "VARCHAR(255)"]
                                       [:user_id :int]
                                       ["FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"]]
                        {:conditional? true}))

(def users-table-ddl
  (jdb/create-table-ddl :users [
                                [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                                [:username "VARCHAR(255)"]]
                        {:conditional? true}))

(def linked-bank-accounts
  (jdb/create-table-ddl :integrated_banks [
                                           [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                                           [:user_id :int]
                                           [:bank "VARCHAR(255)"]
                                           [:encrypted_token "VARCHAR(255)"]
                                           ["FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE"]]
                        {:conditional? true}))

(defn insert [table rows]
  (jdb/insert-multi! mysql-options table rows))

(defn list-table [table]
  (let [table (name table)
        query (str "SELECT * FROM " table)]
    (jdb/query mysql-options [query] {:row-fn identity})))

(defn get-superuser []
  (let [user-id 1
        query (str "SELECT * FROM users WHERE id=" user-id)]
    (jdb/query mysql-options [query] {:row-fn identity})))

; Create tables on service start-up
(jdb/db-do-commands mysql-options [users-table-ddl
                                   linked-bank-accounts
                                   transaction-table-ddl
                                   monobank-accounts-table-ddl
                                   ])