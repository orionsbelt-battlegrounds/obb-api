(defproject obb-api "0.0.1-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}

  ;:java-agents [[com.newrelic.agent.java/newrelic-agent "3.12.0"]]

  :dependencies [[org.clojure/clojure "1.6.0"]
                 [clj-time "0.9.0"]
                 [clj-jwt "0.0.11"]
                 [environ "1.0.0"]
                 [com.novemberain/monger "2.0.1"]

                 [obb-rules "1.10"]

                 [io.pedestal/pedestal.service "0.3.1"]
                 [io.pedestal/pedestal.jetty "0.3.1"]
                 [ch.qos.logback/logback-classic "1.1.2" :exclusions [org.slf4j/slf4j-api]]
                 [org.slf4j/jul-to-slf4j "1.7.10"]
                 [org.slf4j/jcl-over-slf4j "1.7.10"]
                 [org.slf4j/log4j-over-slf4j "1.7.10"]]

  :scm {:name "git"
        :url  "git@github.com:orionsbelt-battlegrounds/obb-api.git"}

  :min-lein-version "2.5.0"
  :resource-paths ["config", "resources"]
  :uberjar-name "obb-api.jar"
  :profiles {:production {:env {:production true}}
             :uberjar {:aot :all}
             :dev
               {:plugins [[com.jakemccrary/lein-test-refresh "0.5.4"]
                          [lein-cloverage "1.0.2"]]
                :aliases {"run-dev" ["trampoline" "run" "-m" "obb-api.server/run-dev"]}
                     :dependencies [[io.pedestal/pedestal.service-tools "0.3.1"]]}}
  :main obb-api.server)

