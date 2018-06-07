(ns maestro.orchestrator
  (:require [maestro.dao :refer :all]))

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

(defn assign-job
  [nu-agent job]
  { "job_id" (get job "id") "agent_id" (get nu-agent "id")})

(defn jobs-performed
  [nu-agent job]
  (if-let [job-type (get-in nu-agent ["jobs_performed" (get job "type")])]
  	(assoc-in nu-agent ["jobs_performed" (get job "type")] (inc job-type))
  	(assoc-in nu-agent ["jobs_performed" (get job "type")] 1)))

(defn update-nu-agent
  [nu-agents nu-agent job]
  (delete-entity nu-agents nu-agent)
  (save-entity nu-agents (jobs-performed nu-agent job)))

(defn finish-job
  [nu-agent nu-agents jobs-assigned on-progress-jobs finished-jobs]
  (let [nu-agent-id (get nu-agent "id")]
  	(when-let [job-assigned (get-entity-by-id jobs-assigned nu-agent-id "agent_id")]
  		(when-let [job (get-entity-by-id on-progress-jobs (get job-assigned "job_id"))]
  			(save-entity finished-jobs job)
  			(update-nu-agent nu-agents nu-agent job)
  			(delete-entity on-progress-jobs job)))))

(defn start-job
  [job jobs on-progress-jobs]
  (save-entity on-progress-jobs job)
  (delete-entity jobs job))

(defn orchestrate
  [job-request nu-agents jobs jobs-assigned job-requests on-progress-jobs finished-jobs]
  (when-let [nu-agent (get-entity-by-id nu-agents (get job-request "agent_id"))]
    (when-let [fittest-job (get-fittest-job nu-agent @jobs)]
      (let [job-assigned (assign-job nu-agent fittest-job)]
      	(save-entity jobs-assigned (assign-job nu-agent fittest-job))
        (finish-job nu-agent nu-agents jobs-assigned on-progress-jobs finished-jobs)
        (start-job fittest-job jobs on-progress-jobs)
        (delete-entity job-requests job-request)
        job-assigned))))
