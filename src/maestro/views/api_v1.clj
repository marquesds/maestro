(ns maestro.views.api-v1
  (:gen-class)
  (:require [io.pedestal.http :as http]
  	        [clojure.data.json :as json]
  	        [schema.core :as s]
  	        [maestro.dao :refer :all]
  	        [maestro.views.schema :refer :all]))

(def nu-agents (atom #{}))

(defn get-nu-agents
  [{{:keys [search]} :query-params}]
  {:body (json/write-str @nu-agents)
  :status 200})

(defn create-nu-agents
  [{:keys [body headers]}]
  (let [input (json/read-str (slurp body))]
  	(s/validate nu-agent-schema input)
  	(save-entity nu-agents input)
  	{:status 201}))

(def routes
  #{["/api/v1/nu-agents/:uuid" :get [get-nu-agents] :route-name ::get-nu-agents]
    ["/api/v1/nu-agents"       :post [create-nu-agents] :route-name ::create-nu-agents]})

(def service
  {::http/type   :jetty
   ::http/routes routes
   ::http/join?  false
   ::http/port   8080})

(defn -main
  [& args]
  (->> service
       http/default-interceptors
       http/dev-interceptors
       http/create-server
       http/start))
