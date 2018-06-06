(ns maestro.views.api-v1
  (:gen-class)
  (:require [io.pedestal.http :as http]
  	        [clojure.data.json :as json]
  	        [schema.core :as s]
  	        [maestro.dao :refer :all]
  	        [maestro.schema :refer :all]))

(def nu-agents (atom #{}))

(defn get-entity
  [coll id]
  (if-let [result (get-entity-by-id coll id)]
    {:body (json/write-str result) :status 200}
    {:body "{}" :status 404}))

(defn get-entities 
  [coll]
  (if (empty? coll)
    {:body "[]" :status 404}
    {:body (json/write-str coll) :status 200}))

(defn save-entity!
  ([coll schema json-input]
   (save-entity! coll schema json-input 201 400))
  ([coll schema json-input success-status]
   (save-entity! coll schema success-status 400))
  ([coll schema json-input success-status error-status]
   (try
     (s/validate schema json-input)
     (save-entity coll json-input)
     {:body "{}" :status success-status}
     (catch Exception e
       {:body (json/write-str {:error (.getMessage e)}) :status error-status}))))

(defn get-nu-agent
  [{{:keys [id]} :path-params}]
  (let [result (get-entity nu-agents id)]
    result))

(defn get-nu-agents 
  [context]
  (if-let [result (get-entities @nu-agents)]
    result))

(defn create-nu-agent
  [{:keys [body headers]}]
  (let [json-input (json/read-str (slurp body))]
  	(let [result (save-entity! nu-agents nu-agent-schema json-input)]
      result)))

(def routes
  #{["/api/v1/nu-agents"     :get [get-nu-agents] :route-name ::get-nu-agents]
    ["/api/v1/nu-agents/:id" :get [get-nu-agent] :route-name ::get-nu-agent]
    ["/api/v1/nu-agents"     :post [create-nu-agent] :route-name ::create-nu-agent]})

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
