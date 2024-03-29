(ns maestro.orchestrator-test
  (:require [clojure.test :refer :all]
  			    [maestro.orchestrator :refer :all]
            [maestro.dao :refer :all]))

(def nu-agents (atom #{}))
(def jobs (atom #{}))
(def on-progress-jobs (atom #{}))
(def finished-jobs (atom #{}))
(def job-requests (atom []))
(def jobs-assigned (atom #{}))

(defn reset-data
  [test-fn]
  (do
    (reset! nu-agents #{})
    (reset! jobs #{})
    (reset! on-progress-jobs #{})
    (reset! finished-jobs #{})
    (reset! job-requests [])
    (reset! jobs-assigned #{}))
  (test-fn))

(use-fixtures :each reset-data)

(defn save-data []
  (save-entity nu-agents 
    {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
     "name" "BoJack Horseman",
     "primary_skillset" ["bills-questions"],
     "secondary_skillset" []})
  (save-entity nu-agents
    {"id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
     "name" "Mr. Peanut Butter",
     "primary_skillset" ["rewards-question"],
     "secondary_skillset" ["bills-questions"]})
  (save-entity jobs 
    {"id" "f26e890b-df8e-422e-a39c-7762aa0bac36",
     "type" "rewards-question",
     "urgent" false})
  (save-entity jobs 
    {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864",
     "type" "bills-questions",
     "urgent" false})
  (save-entity jobs 
    {"id" "c0033410-981c-428a-954a-35dec05ef1d2",
     "type" "bills-questions",
     "urgent" true}))

(deftest urgent-job-test
  (is (true? (urgent? {"urgent" true}))))

(deftest not-urgent-job-test
  (is (false? (urgent? {"urgent" false}))))

(deftest not-urgent-job-without-urgent-key-test
  (is (false? (urgent? {"type" "rewards-question"}))))

(deftest agent-has-job-primary-skillset-test
  (let [nu-agent {"primary_skillset" ["bills-questions"]}
        job {"type" "bills-questions"}]
    (is (true? (primary-skillset? nu-agent job)))))

(deftest agent-has-not-job-primary-skillset-test
  (let [nu-agent {"primary_skillset" ["rewards-question"]}
        job {"type" "bills-questions"}]
    (is (false? (primary-skillset? nu-agent job)))))

(deftest agent-has-job-secondary-skillset-test
  (let [nu-agent {"secondary_skillset" ["bills-questions"]}
        job {"type" "bills-questions"}]
    (is (true? (secondary-skillset? nu-agent job)))))

(deftest agent-has-not-job-secondary-skillset-test
  (let [nu-agent {"secondary_skillset" ["rewards-question"]}
        job {"type" "bills-questions"}]
    (is (false? (secondary-skillset? nu-agent job)))))

(deftest job-urgent-and-agent-with-job-primary-skillset-test
  (let [nu-agent {"primary_skillset" ["bills-questions"]}
        job {"type" "bills-questions" "urgent" true}]
    (is (true? (urgent-with-primary-skillset? nu-agent job)))))

(deftest job-not-urgent-and-agent-with-job-primary-skillset-test
  (let [nu-agent {"primary_skillset" ["bills-questions"]}
        job {"type" "bills-questions" "urgent" false}]
    (is (false? (urgent-with-primary-skillset? nu-agent job)))))

(deftest job-urgent-and-agent-with-job-secondary-skillset-test
  (let [nu-agent {"primary_skillset" ["rewards-question"] 
  	              "secondary_skillset" ["bills-questions"]}
        job {"type" "bills-questions" "urgent" true}]
    (is (true? (urgent-with-secondary-skillset? nu-agent job)))))

(deftest job-not-urgent-and-agent-with-job-secondary-skillset-test
  (let [nu-agent {"primary_skillset" ["rewards-question"] 
  	              "secondary_skillset" ["bills-questions"]}
        job {"type" "bills-questions" "urgent" false}]
    (is (false? (urgent-with-secondary-skillset? nu-agent job)))))

(deftest filter-job-with-fn-is-urgent-with-primary-skillset-test
  (let [nu-agent {"primary_skillset" ["bills-questions"]}
        jobs [{"type" "bills-questions" "urgent" false}
              {"type" "bills-questions" "urgent" true}]]
    (is (= {"type" "bills-questions" "urgent" true}
      (filter-job nu-agent jobs urgent-with-primary-skillset?)))))

(deftest filter-job-with-fn-is-urgent-with-primary-skillset-not-found-test
  (let [nu-agent {"primary_skillset" ["bills-questions"]}
        jobs [{"id" "1111" "type" "bills-questions" "urgent" false} 
              {"id" "2222" "type" "bills-questions" "urgent" false}]]
    (is (nil? (filter-job nu-agent jobs urgent-with-primary-skillset?)))))

(deftest get-fittest-job-that-is-urgent-with-primary-skillset-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] 
  	              "secondary_skillset" ["rewards-question"]}
        jobs [{"type" "bills-questions" "urgent" true} 
              {"type" "bills-questions" "urgent" false}]]
    (is (= {"type" "bills-questions" "urgent" true} (get-fittest-job nu-agent jobs)))))

(deftest get-fittest-job-that-is-not-urgent-with-primary-skillset-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] 
  	              "secondary_skillset" ["rewards-question"]}
        jobs [{"type" "bills-questions" "urgent" false} 
              {"type" "rewards-question" "urgent" false}]]
    (is (= {"type" "bills-questions" "urgent" false} (get-fittest-job nu-agent jobs)))))

(deftest get-fittest-job-that-is-urgent-with-secondary-skillset-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] 
  	              "secondary_skillset" ["rewards-question"]}
        jobs [{"type" "bills-questions" "urgent" false} 
              {"type" "rewards-question" "urgent" true}]]
    (is (= {"type" "rewards-question" "urgent" true} (get-fittest-job nu-agent jobs)))))

(deftest get-fittest-job-that-is-not-urgent-with-secondary-skillset-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] 
  	              "secondary_skillset" ["rewards-question"]}
        jobs [{"type" "bills-questions" "urgent" false} 
              {"type" "rewards-question" "urgent" false}]]
    (is (= {"type" "rewards-question" "urgent" false} (get-fittest-job nu-agent jobs)))))

(deftest get-fittest-job-that-is-not-urgent-with-no-skillset-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["other-questions"] 
  	              "secondary_skillset" ["magic-question"]}
        jobs [{"type" "bills-questions" "urgent" false} 
              {"type" "rewards-question" "urgent" false}]]
    (is (nil? (get-fittest-job nu-agent jobs)))))

(deftest assign-job-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] 
  	              "secondary_skillset" ["rewards-question"]}
        job {"id" "3221" "type" "bills-questions" "urgent" false}]
    (is (= { "job_id" "3221" "agent_id" "1234"} (assign-job nu-agent job)))))

(deftest jobs-performed-test
  (let [nu-agent {"id" "1234" "primary_skillset" ["bills-questions"] 
                  "secondary_skillset" ["rewards-question"]}
        first-job {"id" "3221" "type" "bills-questions" "urgent" false}
        second-job {"id" "1324" "type" "bills-questions" "urgent" false}
        third-job {"id" "4444" "type" "rewards-questions" "urgent" false}]
    (let [nu-agent (jobs-performed nu-agent first-job)]
      (is (= (get-in nu-agent ["jobs_performed" "bills-questions"]) 1))
      (let [nu-agent (jobs-performed nu-agent second-job)]
        (is (= (get-in nu-agent ["jobs_performed" "bills-questions"]) 2))
        (let [nu-agent (jobs-performed nu-agent third-job)]
          (is (= (get-in nu-agent ["jobs_performed" "bills-questions"]) 2))
          (is (= (get-in nu-agent ["jobs_performed" "rewards-questions"]) 1)))))))

(deftest finish-old-job-test
  (save-data)
  (let [job-request {"agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}]
    (let [job-assigned (orchestrate job-request nu-agents 
                        jobs jobs-assigned job-requests finished-jobs
                        on-progress-jobs)]
      (is (= "c0033410-981c-428a-954a-35dec05ef1d2" (get job-assigned "job_id")))
      (let [nu-agent (get-entity-by-id nu-agents (get job-request "agent_id"))]
        (finish-job nu-agent nu-agents jobs-assigned on-progress-jobs finished-jobs)
        (let [job (get-entity-by-id finished-jobs "c0033410-981c-428a-954a-35dec05ef1d2")]
          (is (complement nil?) job)
          (is (nil? (some #(= job %) @on-progress-jobs))))))))

(deftest start-new-job-test
  (let [job {"id" "c0033410-981c-428a-954a-35dec05ef1d2" 
             "type" "bills-questions" "urgent" true}]
    (save-entity jobs job)
    (start-job job jobs on-progress-jobs)
    (is (nil? (some #(= job %) @jobs)))
    (is (complement nil?) (some #(= job %) @on-progress-jobs))))

(deftest orchestrate-test
  (save-data)
  (let [job-request {"agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}]
    (let [job-assigned (orchestrate job-request nu-agents 
                        jobs jobs-assigned job-requests
                        on-progress-jobs finished-jobs)]
      (is (= job-assigned {"job_id" "c0033410-981c-428a-954a-35dec05ef1d2",
                           "agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}))))
  (let [job-request {"agent_id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"}]
    (let [job-assigned (orchestrate job-request nu-agents 
                        jobs jobs-assigned job-requests 
                        on-progress-jobs finished-jobs)]
      (is (= job-assigned {"job_id" "f26e890b-df8e-422e-a39c-7762aa0bac36",
                           "agent_id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"})))))

(deftest orchestrate-flow-job-on-progress-test
  (save-data)
  (let [job-request {"agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}]
    (orchestrate job-request nu-agents 
                 jobs jobs-assigned job-requests
                 on-progress-jobs finished-jobs)
    (is (not (nil? (get-entity-by-id on-progress-jobs "c0033410-981c-428a-954a-35dec05ef1d2"))))))

(deftest orchestrate-flow-job-finished-test
  (save-data)
  (let [job-request {"agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}]
    (orchestrate job-request nu-agents 
                 jobs jobs-assigned job-requests 
                 on-progress-jobs finished-jobs)
    (is (not (nil? (get-entity-by-id on-progress-jobs "c0033410-981c-428a-954a-35dec05ef1d2"))))
    (orchestrate job-request nu-agents 
                 jobs jobs-assigned job-requests 
                 on-progress-jobs finished-jobs)
    (is (not (nil? (get-entity-by-id on-progress-jobs "690de6bc-163c-4345-bf6f-25dd0c58e864"))))
    (is (nil? (get-entity-by-id on-progress-jobs "c0033410-981c-428a-954a-35dec05ef1d2")))
    (is (not (nil? (get-entity-by-id finished-jobs "c0033410-981c-428a-954a-35dec05ef1d2"))))))
