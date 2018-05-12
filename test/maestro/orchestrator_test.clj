(ns maestro.orchestrator-test
  (:require [clojure.test :refer :all]
  			[maestro.orchestrator :refer :all]))


(defn reset-data 
  [test-fn]
  (do
    (reset! agents #{})
    (reset! jobs #{})
    (reset! job-requests [])
    (reset! jobs-assigned #{}))
  (test-fn))

(use-fixtures :each reset-data)

(deftest test-get-entity
	(let [nu-agent {"new_agent" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
		(let [entity (get-entity nu-agent)]
			(is (= entity (get nu-agent "new_agent"))))))

(deftest test-try-get-not-existent-entity
	(let [nu-developer {"new_developer" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
		(let [entity (get-entity nu-developer)]
			(is (nil? entity)))))

(deftest test-get-agents-collection
	(let [collection (get-collection {"new_agent" {"id" 1234}})]
		(is (= collection agents))))

(deftest test-get-jobs-collection
	(let [collection (get-collection {"new_job" {"id" 3221}})]
		(is (= collection jobs))))

(deftest test-get-job-requests-collection
	(let [collection (get-collection {"job_request" {"id" 9887}})]
		(is (= collection job-requests))))

(deftest test-get-jobs-assigned-collection
	(let [collection (get-collection {"job_assigned" {"job_id" 3221 "agent_id" 1234}})]
		(is (= collection jobs-assigned))))

(deftest test-get-non-existent-collection
	(let [collection (get-collection {"not_found" {"id" 404}})]
		(is (nil? collection))))

(deftest test-urgent-job
	(is (true? (urgent? {"urgent" true}))))

(deftest test-not-urgent-job
	(is (false? (urgent? {"urgent" false}))))

(deftest test-not-urgent-job-without-urgent-key
	(is (false? (urgent? {"type" "rewards-question"}))))

(deftest test-agent-has-job-primary-skillset
	(let [nu-agent {"primary_skillset" ["bills-questions"]}
		job {"type" "bills-questions"}]
		(is (true? (has-primary-skillset? nu-agent job)))))

(deftest test-agent-has-not-job-primary-skillset
	(let [nu-agent {"primary_skillset" ["rewards-question"]}
		job {"type" "bills-questions"}]
		(is (false? (has-primary-skillset? nu-agent job)))))

(deftest test-agent-has-job-secondary-skillset
	(let [nu-agent {"secondary_skillset" ["bills-questions"]}
		job {"type" "bills-questions"}]
		(is (true? (has-secondary-skillset? nu-agent job)))))

(deftest test-agent-has-not-job-secondary-skillset
	(let [nu-agent {"secondary_skillset" ["rewards-question"]}
		job {"type" "bills-questions"}]
		(is (false? (has-secondary-skillset? nu-agent job)))))

(deftest test-job-urgent-and-agent-with-job-primary-skillset
	(let [nu-agent {"primary_skillset" ["bills-questions"]}
		job {"type" "bills-questions" "urgent" true}]
		(is (true? (is-urgent-with-primary-skillset? nu-agent job)))))

(deftest test-job-not-urgent-and-agent-with-job-primary-skillset
	(let [nu-agent {"primary_skillset" ["bills-questions"]}
		job {"type" "bills-questions" "urgent" false}]
		(is (false? (is-urgent-with-primary-skillset? nu-agent job)))))

(deftest test-job-urgent-and-agent-with-job-secondary-skillset
	(let [nu-agent {"primary_skillset" ["rewards-question"] "secondary_skillset" ["bills-questions"]}
		job {"type" "bills-questions" "urgent" true}]
		(is (true? (is-urgent-with-secondary-skillset? nu-agent job)))))

(deftest test-job-not-urgent-and-agent-with-job-secondary-skillset
	(let [nu-agent {"primary_skillset" ["rewards-question"] "secondary_skillset" ["bills-questions"]}
		job {"type" "bills-questions" "urgent" false}]
		(is (false? (is-urgent-with-secondary-skillset? nu-agent job)))))

(deftest test-filter-job-with-fn-is-urgent-with-primary-skillset
	(let [nu-agent {"primary_skillset" ["bills-questions"]}
		jobs [{"type" "bills-questions" "urgent" false} {"type" "bills-questions" "urgent" true}]]
		(is (= {"type" "bills-questions" "urgent" true}  (filter-job nu-agent jobs is-urgent-with-primary-skillset?)))))

(deftest test-filter-job-with-fn-is-urgent-with-primary-skillset-not-found
	(let [nu-agent {"primary_skillset" ["bills-questions"]}
		jobs [{"id" "1111" "type" "bills-questions" "urgent" false} {"id" "2222" "type" "bills-questions" "urgent" false}]]
		(is (nil? (filter-job nu-agent jobs is-urgent-with-primary-skillset?)))))

(deftest test-save-entities
	(let [input-json [{"new_agent" {"id" 1234}} {"new_job" {"id" 3221}} {"job_request" {"id" 9887}}]]
		(save-entities input-json)
		(is (= (first @agents) {"id" 1234}))
		(is (= (first @jobs) {"id" 3221}))
		(is (= (first @job-requests) {"id" 9887}))))

(deftest test-get-fittest-job-that-is-urgent-with-primary-skillset
	(let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] "secondary_skillset" ["rewards-question"]}
		jobs [{"type" "bills-questions" "urgent" true} {"type" "bills-questions" "urgent" false}]]
		(is (= {"type" "bills-questions" "urgent" true} (get-fittest-job nu-agent jobs)))))

(deftest test-get-fittest-job-that-is-not-urgent-with-primary-skillset
	(let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] "secondary_skillset" ["rewards-question"]}
		jobs [{"type" "bills-questions" "urgent" false} {"type" "rewards-question" "urgent" false}]]
		(is (= {"type" "bills-questions" "urgent" false} (get-fittest-job nu-agent jobs)))))

(deftest test-get-fittest-job-that-is-urgent-with-secondary-skillset
	(let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] "secondary_skillset" ["rewards-question"]}
		jobs [{"type" "bills-questions" "urgent" false} {"type" "rewards-question" "urgent" true}]]
		(is (= {"type" "rewards-question" "urgent" true} (get-fittest-job nu-agent jobs)))))

(deftest test-get-fittest-job-that-is-not-urgent-with-secondary-skillset
	(let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] "secondary_skillset" ["rewards-question"]}
		jobs [{"type" "bills-questions" "urgent" false} {"type" "rewards-question" "urgent" false}]]
		(is (= {"type" "rewards-question" "urgent" false} (get-fittest-job nu-agent jobs)))))

(deftest test-get-fittest-job-that-is-not-urgent-with-no-skillset
	(let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] "secondary_skillset" ["magic-question"]}
		jobs [{"type" "bills-questions" "urgent" false} {"type" "rewards-question" "urgent" false}]]
		(is (nil? (get-fittest-job nu-agent jobs)))))
