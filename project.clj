(defproject app-hacking "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :main app-hacking.main
  :aot [app-hacking.main]
  :uberjar-name "app-hacking-standalone.jar"
  ;; :plugins [[lein-swank "1.4.4"]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.cli "1.0.206"]
                 [compojure "1.6.2"]
                 [ring/ring-core "1.9.4"]

                 [org.clojure/data.json "2.4.0"]

                 [http-kit "2.5.3"]

                 [http-kit/dbcp "0.1.0"] ;; database access
                 [org.clojure/java.jdbc "0.7.12"]
                 [mysql/mysql-connector-java "8.0.25"] ;; mysql jdbc driver

                 ;; [org.fressian/fressian "0.6.3"]

                 ;; for serialization clojure object to bytes
                 ;; [com.taoensso/nippy "1.1.0"]

                 ;; Redis client & message queue
                 ;; [com.taoensso/carmine "1.5.0"]

                 ;; logging,  another option [com.taoensso/timbre "1.5.2"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.7"]
                 ;; template
                 [me.shenfeng/mustache "1.1"]])
