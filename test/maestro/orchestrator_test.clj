(ns maestro.orchestrator-test
  (:require [clojure.test :refer :all]
  			[maestro.orchestrator :refer :all]))

(defn reset-data 
  [test-fn]
  (do
    (reset! nu-agents #{})
    (reset! jobs #{})
    (reset! job-requests [])
    (reset! jobs-assigned #{}))
  (test-fn))

(use-fixtures :each reset-data)

(def input-json 
[
  {
    "new_agent" {
      "id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260",
      "name" "BoJack Horseman",
      "primary_skillset" ["bills-questions"],
      "secondary_skillset" []
    }
  },
  {
    "new_job" {
      "id" "f26e890b-df8e-422e-a39c-7762aa0bac36",
      "type" "rewards-question",
      "urgent" false
    }
  },
  {
    "new_agent" {
      "id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88",
      "name" "Mr. Peanut Butter",
      "primary_skillset" ["rewards-question"],
      "secondary_skillset" ["bills-questions"]
    }
  },
  {
    "new_job" {
      "id" "690de6bc-163c-4345-bf6f-25dd0c58e864",
      "type" "bills-questions",
      "urgent" false
    }
  },
  {
    "new_job" {
      "id" "c0033410-981c-428a-954a-35dec05ef1d2",
      "type" "bills-questions",
      "urgent" true
    }
  },
  {
    "job_request" {
      "agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
    }
  },
  {
    "job_request" {
      "agent_id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
    }
  }
])

(def output-json 
#{{
	"job_assigned" {
	  "job_id" "c0033410-981c-428a-954a-35dec05ef1d2",
	  "agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
	}
  },
  {
    "job_assigned" {
      "job_id" "f26e890b-df8e-422e-a39c-7762aa0bac36",
      "agent_id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
    }
  }
})

(deftest get-entity-test
  (let [nu-agent {"new_agent" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
    (let [entity (get-entity nu-agent)]
      (is (= entity (get nu-agent "new_agent"))))))

(deftest try-get-not-existent-entity-test
  (let [nu-developer {"new_developer" {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}}]
    (let [entity (get-entity nu-developer)]
      (is (nil? entity)))))

(deftest get-nu-agents-collection-test
  (let [coll (get-collection {"new_agent" {"id" "1234"}})]
    (is (= coll nu-agents))))

(deftest get-jobs-collection-test
  (let [coll (get-collection {"new_job" {"id" "3221"}})]
    (is (= coll jobs))))

(deftest get-job-requests-collection-test
  (let [coll (get-collection {"job_request" {"id" "9887"}})]
    (is (= coll job-requests))))

(deftest get-jobs-assigned-collection-test
  (let [coll (get-collection {"job_assigned" {"job_id" "3221" "agent_id" "1234"}})]
    (is (= coll jobs-assigned))))

(deftest get-non-existent-collection-test
  (let [coll (get-collection {"not_found" {"id" "404"}})]
    (is (nil? coll))))

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

(deftest save-entities-test
  (let [input-json [{"new_agent" {"id" "1234"}} 
  	                {"new_job" {"id" "3221"}} 
  	                {"job_request" {"id" "9887"}}]]
    (save-entities input-json)
    (is (= (first @nu-agents) {"id" "1234"}))
    (is (= (first @jobs) {"id" "3221"}))
    (is (= (first @job-requests) {"id" "9887"}))))

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
    (is (= {"job_assigned" { "job_id" "3221" "agent_id" "1234"}} (assign-job nu-agent job)))))

(deftest orchestrate-test
  (let [output (orchestrate input-json)]
    (is (= output output-json))
    (is (= [{"id" "690de6bc-163c-4345-bf6f-25dd0c58e864" 
    	     "type" "bills-questions" "urgent" false}] @jobs))
    (is (empty? @job-requests))))
