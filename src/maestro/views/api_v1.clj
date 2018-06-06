(ns maestro.views.api-v1
  (:gen-class)
  (:require [io.pedestal.http :as http]
  	        [clojure.data.json :as json]
  	        [schema.core :as s]
  	        [maestro.dao :refer :all]
            [maestro.orchestrator :refer :all]
  	        [maestro.schema :refer :all]))

(def headers {"Content-Type" "application/json"})

(def nu-agents (atom #{}))
(def jobs (atom #{}))
(def job-requests (atom []))
(def jobs-assigned (atom #{}))

(defn not-found
  [body]
  {:body body :status 404 :headers headers})

(defn get-entity
  [coll id]
  (if-let [result (get-entity-by-id coll id)]
    {:body (json/write-str result) :status 200 :headers headers}
    (not-found "{}")))

(defn get-entities 
  [coll]
  (if (empty? coll)
    (not-found "[]")
    {:body (json/write-str coll) :status 200 :headers headers}))

(defn save-entity!
  ([f coll schema json-input]
   (save-entity! f coll schema json-input 201 400))
  ([f coll schema json-input success-status]
   (save-entity! f coll schema success-status 400))
  ([f coll schema json-input success-status error-status]
   (try
     (s/validate schema json-input)
     (f coll json-input)
     {:body "{}" :status success-status :headers headers}
     (catch Exception e
       {:body (json/write-str {:error (.getMessage e)}) 
        :status error-status
        :headers headers}))))

(defn assign
  [json-input]
  (if-let [nu-agent (get-entity-by-id nu-agents (get json-input "agent_id"))]
      (if-let [fittest-job (get-fittest-job nu-agent @jobs)]
        (do
          (save-entity jobs-assigned (assign-job nu-agent fittest-job))
          {:body (json/write-str fittest-job) :status 201 :headers headers})
        (not-found "{}"))
      (not-found "{}")))

(defn get-nu-agent
  [{{:keys [id]} :path-params}]
  (let [result (get-entity nu-agents id)]
    result))

(defn get-nu-agents 
  [context]
  (if-let [result (get-entities @nu-agents)]
    result))

(defn create-nu-agent
  [{:keys [body]}]
  (let [json-input (json/read-str (slurp body))]
  	(let [result (save-entity! save-entity nu-agents nu-agent-schema json-input)]
      result)))

(defn get-job
  [{{:keys [id]} :path-params}]
  (let [result (get-entity jobs id)]
    result))

(defn get-jobs
  [context]
  (if-let [result (get-entities @jobs)]
    result))

(defn create-job
  [{:keys [body]}]
  (let [json-input (json/read-str (slurp body))]
    (let [result (save-entity! save-entity jobs job-schema json-input)]
      result)))

(defn get-job-requests
  [context]
  (if-let [result (get-entities @job-requests)]
    result))

(defn create-job-request
  [{:keys [body]}]
  (let [json-input (json/read-str (slurp body))]
    (let [result (save-entity! save-entity-keep-order job-requests 
                               job-request-schema json-input)]
      (if (= 201 (:status result))
        (if-let [job-assigned (orchestrate json-input nu-agents 
                                           jobs jobs-assigned job-requests)]
          {:body (json/write-str job-assigned) :status 201 :headers headers}
          (not-found "{}"))
        result))))

(def routes
  #{["/api/v1/nu-agents"        :get [get-nu-agents] :route-name ::get-nu-agents]
    ["/api/v1/nu-agents/:id"    :get [get-nu-agent] :route-name ::get-nu-agent]
    ["/api/v1/nu-agents"        :post [create-nu-agent] :route-name ::create-nu-agent]
    ["/api/v1/jobs"             :get [get-jobs] :route-name ::get-jobs]
    ["/api/v1/jobs/:id"         :get [get-job] :route-name ::get-job]
    ["/api/v1/jobs"             :post [create-job] :route-name ::create-job]
    ["/api/v1/job-requests"     :get [get-job-requests] :route-name ::get-job-requests]
    ["/api/v1/job-requests"     :post [create-job-request] :route-name ::create-job-request]})

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
