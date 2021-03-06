(ns app-hacking.routes
  (:use [compojure.core :only [defroutes GET POST DELETE ANY context]]
        (ring.middleware [keyword-params :only [wrap-keyword-params]]
                         [params :only [wrap-params]]
                         [session :only [wrap-session]])
        [app-hacking.middleware :only [wrap-failsafe wrap-request-logging-in-dev
                                       wrap-reload-in-dev JGET JPUT JPOST JDELETE]])
  (:require [app-hacking.handlers.app :as app]
            [app-hacking.handlers.api :as api]
            [app-hacking.handlers.dao :as dao]
              [compojure.route :as route]))

;; define mapping here
(defroutes server-routes*
           (GET "/" [] app/show-landing)
           (GET "/health" [] api/get-health)
  (context "/api" []
             ;; JGET returns json encoding of the response
             (JGET "/time" [] api/get-time)
             (JGET "/mono" [] api/monobank)
             (JGET "/transactions" [] dao/list-transactions)
             (JGET "/accounts" [] dao/list-accounts)
             (JGET "/users" [] dao/list-users)
             )
           ;; static files under ./public folder, prefix /static
           ;; like /static/css/style.css
           (route/files "/static")
           ;; 404, modify for a better 404 page
           (route/not-found "<p>Page not found.</p>"))

(defn app [] (-> #'server-routes*
                 wrap-session
                 wrap-keyword-params
                 wrap-params
                 wrap-request-logging-in-dev
                 wrap-reload-in-dev
                 wrap-failsafe))
