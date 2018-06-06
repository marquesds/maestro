(defproject maestro "1.0.0"
  :description "Assign fittest job to correct nu-agents"
  :url "https://gitlab.com/marquesds/maestro/tree/master"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/tools.cli "0.3.7"]
                 [io.pedestal/pedestal.service "0.5.3"]
                 [io.pedestal/pedestal.jetty "0.5.3"]
                 [org.slf4j/slf4j-simple "1.7.25"]
                 [prismatic/schema "1.1.9"]]
  :main ^:skip-aot maestro.views.api-v1
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
