(ns maestro.orchestrator
  (:require [maestro.dao :refer :all]))

(def agents (atom #{}))
(def jobs (atom #{}))
(def job-requests (atom []))
(def jobs-assigned (atom #{}))

(defn get-entity [element]
  (let [nu-agent (get element "new_agent")
        job (get element "new_job")
        job-request (get element "job_request")]
    (first (filter (fn [x] (not (nil? x))) [nu-agent job job-request]))))

(defn get-collection 
  [element]
  (cond 
    (not (nil? (get element "new_agent"))) agents
    (not (nil? (get element "new_job"))) jobs
    (not (nil? (get element "job_request"))) job-requests
    (not (nil? (get element "job_assigned"))) jobs-assigned))

(defn urgent? 
  [job]
  (boolean (get job "urgent")))

(defn primary-skillset? 
  [nu-agent job]
  (.contains (get nu-agent "primary_skillset") (get job "type")))

(defn secondary-skillset?
  [nu-agent job]
  (.contains (get nu-agent "secondary_skillset") (get job "type")))

(defn urgent-with-primary-skillset? 
  [nu-agent job]
  (and (urgent? job) (primary-skillset? nu-agent job)))

(defn urgent-with-secondary-skillset? 
  [nu-agent job]
  (and (urgent? job) (secondary-skillset? nu-agent job)))

(defn filter-job
  [nu-agent jobs filter-fn]
  (first (filter (fn [job] (filter-fn nu-agent job)) jobs)))

(def precedence-functions [urgent-with-primary-skillset? primary-skillset? 
                           urgent-with-secondary-skillset? secondary-skillset?])

(defn get-fittest-job
  [nu-agent jobs]
  (first (filter (complement nil?) (map (partial filter-job nu-agent jobs) precedence-functions))))

(defn save-entities
  [input-json]
  (doseq [element input-json]
  	(let [collection (get-collection element)]
  	  (if (= collection jobs)
  	    (save-entity-keep-order collection (get-entity element))
        (save-entity collection (get-entity element))))))

(defn assign-job
  [nu-agent job]
  {"job_assigned" { "job_id" (get job "id") "agent_id" (get nu-agent "id")}})

(defn orchestrate
  [input-json]
  (save-entities input-json)
  (doseq [job-request @job-requests]
    (let [nu-agent (get-entity-by-id agents (get job-request "agent_id"))]
      (when-let [fittest-job (get-fittest-job nu-agent @jobs)]
        (save-entity jobs-assigned (assign-job nu-agent fittest-job))
        (delete-entity jobs fittest-job)
        (delete-entity job-requests job-request))))
  @jobs-assigned)
