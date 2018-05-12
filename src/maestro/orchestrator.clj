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

(defn has-primary-skillset? 
	[nu-agent job]
	(.contains (get nu-agent "primary_skillset") (get job "type")))

(defn has-secondary-skillset? 
	[nu-agent job]
	(.contains (get nu-agent "secondary_skillset") (get job "type")))

(defn is-urgent-with-primary-skillset? 
	[nu-agent job]
	(and (urgent? job) (has-primary-skillset? nu-agent job)))

(defn is-urgent-with-secondary-skillset? 
	[nu-agent job]
	(and (urgent? job) (has-secondary-skillset? nu-agent job)))

(defn filter-job
	[nu-agent jobs filter-fn]
	(first (filter (fn [job] (filter-fn nu-agent job)) jobs)))

(def precedence-functions [is-urgent-with-primary-skillset? has-primary-skillset? 
	is-urgent-with-secondary-skillset? has-secondary-skillset?])

(defn get-fittest-job
	[nu-agent jobs]
	(first (filter (complement nil?) (map (partial filter-job nu-agent jobs) precedence-functions))))

(defn save-entities
  [input-json]
  (doseq [element input-json]
  	(save-entity (get-collection element) (get-entity element))))
