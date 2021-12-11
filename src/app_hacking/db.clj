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

()

(def transaction-table-ddl
  (jdb/create-table-ddl :mono_transaction [
                         [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                         [:amount "BIGINT"]
                         [:comment "VARCHAR(255)"]
                         [:mcc :int]]
                        {:conditional? true}))

(def accounts-table-ddl
  (jdb/create-table-ddl :mono_account [
                                           [:id :int "AUTO_INCREMENT PRIMARY KEY"]
                                           [:mono_id :int]
                                           [:account_id "VARCHAR(255)"]]
                        {:conditional? true}))

(jdb/db-do-commands mysql-options [transaction-table-ddl accounts-table-ddl])