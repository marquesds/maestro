(ns maestro.orchestrator-test
  (:require [clojure.test :refer :all]
  			[maestro.orchestrator :refer :all]))


(defn reset-data 
  [test-fn]
  (do
    (reset! agents #{})
    (reset! jobs #{})
    (reset! job-requests []))
  (test-fn))

(use-fixtures :each reset-data)

(deftest test-get-entity-from-dict
	(let [nu-agent {"new_agent" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
		(let [entity (get-entity-from-dict nu-agent)]
			(is (= entity (get nu-agent "new_agent"))))))

(deftest test-try-get-not-existent-entity
	(let [nu-developer {"new_developer" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
		(let [entity (get-entity-from-dict nu-developer)]
			(is (nil? entity)))))

(deftest test-save-entities
	(let [input-json [{"new_agent" {"id" 1234}} {"new_job" {"id" 3221}} {"job_request" {"id" 9887}}]]
		(save-entities input-json)
		(is (= (first @agents) {"new_agent" {"id" 1234}}))
		(is (= (first @jobs) {"new_job" {"id" 3221}}))
		(is (= (first @job-requests) {"job_request" {"id" 9887}}))))
