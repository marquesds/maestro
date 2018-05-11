(ns maestro.orchestrator
  (:require [maestro.dao :refer :all]))


(def agents (atom #{}))

(def jobs (atom #{}))

(def job-requests (atom []))

(defn get-entity-from-dict [element]
	(let [nu-agent (get element "new_agent")
		job (get element "new_job")
		job-request (get element "job_request")]
		(first (filter (fn [x] (not (nil? x))) [nu-agent job job-request]))))

(defn save-entities
  [input-json]
  (doseq [element input-json]
  	(save-entity (get-entity-from-dict element))))
