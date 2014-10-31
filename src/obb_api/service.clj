(ns obb-api.service
  (:require [io.pedestal.http :as bootstrap]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.http.route.definition :refer [defroutes]]
            [ring.util.response :as ring-resp]
            [obb-api.handlers.index :as index]
            [obb-api.interceptors.auth-interceptor :as auth-interceptor]))

(defroutes routes
  [[["/" {:get index/handler}
     ^:interceptors [auth-interceptor/parse]

     ^:interceptors [auth-interceptor/verify]
     "/auth/verify" {:get index/handler}]]])

;; Consumed by obb-api.server/create-server
;; See bootstrap/default-interceptors for additional options you can configure
(def service {:env :prod
              ;; You can bring your own non-default interceptors. Make
              ;; sure you include routing and set it up right for
              ;; dev-mode. If you do, many other keys for configuring
              ;; default interceptors will be ignored.
              ;; :bootstrap/interceptors []
              ::bootstrap/routes routes

              ;; Uncomment next line to enable CORS support, add
              ;; string(s) specifying scheme, host and port for
              ;; allowed source(s):
              ;;
              ;; "http://localhost:8080"
              ;;
              ::bootstrap/allowed-origins ["http://www.orionsbelt.eu:80"]

              ;; Root for resource interceptor that is available by default.
              ::bootstrap/resource-path "/public"

              ;; Either :jetty, :immutant or :tomcat (see comments in project.clj)
              ::bootstrap/type :jetty
              ;;::bootstrap/host "localhost"
              ::bootstrap/port (Integer/parseInt (get (System/getenv) "PORT" "8080"))})

