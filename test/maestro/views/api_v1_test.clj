(ns maestro.views.api-v1-test
  (:require [clojure.test :refer :all]
  	        [io.pedestal.http :as http]
            [io.pedestal.test :as pedestal-test]
            [clojure.data.json :as json]
            [maestro.views.api-v1 :refer :all]
  	        [maestro.dao :refer :all]))

(defn reset-data 
  [test-fn]
  (do
    (reset! nu-agents #{})
    (reset! jobs #{})
    (reset! job-requests [])
    (reset! jobs-assigned #{})
    (reset! finished-jobs #{})
    (reset! jobs-on-progress #{}))
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

(def valid-nu-agent-input 
  (json/write-str {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
                   "name" "BoJack Horseman"
                   "primary_skillset" ["bills-questions"]
                   "secondary_skillset" []}))
(def invalid-nu-agent-input
  (json/write-str {"id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"
                   "name" 123456
                   "primary_skillset" ["bills-questions"]
                   "secondary_skillset" []}))

(def valid-job-input 
  (json/write-str {"id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
                   "type" "rewards-question"
                   "urgent" false}))
(def invalid-job-input
  (json/write-str {"id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88"
                   "type" "rewards-question"
                   "urgent" 1}))

(def valid-job-request-input 
  (json/write-str {"agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"}))
(def invalid-job-request-input
  (json/write-str {"agent_id" false}))

(def nu-agent-validation-output
  "{\"error\":\"Value does not match schema: {\\\"name\\\" (not (instance? java.lang.String 123456))}\"}")

(def job-validation-output
  "{\"error\":\"Value does not match schema: {\\\"urgent\\\" (not (instance? java.lang.Boolean 1))}\"}")

(def job-request-validation-output
  "{\"error\":\"Value does not match schema: {\\\"agent_id\\\" (not (instance? java.lang.String false))}\"}")

(def service-fn
  (::http/service-fn (http/create-servlet {::http/routes routes ::http/port 9000})))

(deftest get-nu-agent-test
  (save-entity nu-agents (json/read-str valid-nu-agent-input))
  (let [response (pedestal-test/response-for service-fn 
  	              :get "/api/v1/nu-agents/8ab86c18-3fae-4804-bfd9-c3d6e8f66260")]
    (is (= 200 (:status response)))
  	(is (= valid-nu-agent-input (:body response)))))

(deftest get-nu-agent-not-found-test
  (let [response (pedestal-test/response-for service-fn 
  	              :get "/api/v1/nu-agents/8ab86c18-3fae-4804-bfd9-c3d6e8f66260")]
    (is (= 404 (:status response)))
  	(is (= "{}" (:body response)))))

(deftest get-nu-agents-test
  (save-entity nu-agents (json/read-str valid-nu-agent-input))
  (let [response (pedestal-test/response-for service-fn 
  	              :get "/api/v1/nu-agents")]
    (is (= 200 (:status response)))
  	(is (= (json/write-str @nu-agents) (:body response)))))

(deftest get-nu-agents-not-found-test
  (let [response (pedestal-test/response-for service-fn 
  	              :get "/api/v1/nu-agents")]
    (is (= 404 (:status response)))
  	(is (= "[]" (:body response)))))

(deftest create-nu-agent-test
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/nu-agents" 
  	                                                    :headers {"Content-Type" "application/json"}
  	                                                    :body valid-nu-agent-input)]
    (is (= 201 (:status response)))
    (is (= "{}" (:body response)))
    (is (= #{(json/read-str valid-nu-agent-input)} @nu-agents))))

(deftest create-nu-agent-failed-test
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/nu-agents" 
  	                                                    :headers {"Content-Type" "application/json"}
  	                                                    :body invalid-nu-agent-input)]
    (is (= 400 (:status response)))
    (is (= nu-agent-validation-output (:body response)))))

(deftest get-job-test
  (save-entity jobs (json/read-str valid-job-input))
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/jobs/ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")]
    (is (= 200 (:status response)))
    (is (= valid-job-input (:body response)))))

(deftest get-job-not-found-test
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/jobs/ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")]
    (is (= 404 (:status response)))
    (is (= "{}" (:body response)))))

(deftest get-jobs-test
  (save-entity jobs (json/read-str valid-job-input))
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/jobs")]
    (is (= 200 (:status response)))
    (is (= (json/write-str @jobs) (:body response)))))

(deftest get-jobs-not-found-test
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/jobs")]
    (is (= 404 (:status response)))
    (is (= "[]" (:body response)))))

(deftest create-job-test
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/jobs" 
                                                        :headers {"Content-Type" "application/json"}
                                                        :body valid-job-input)]
    (is (= 201 (:status response)))
    (is (= "{}" (:body response)))
    (is (= #{(json/read-str valid-job-input)} @jobs))))

(deftest create-job-failed-test
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/jobs" 
                                                        :headers {"Content-Type" "application/json"}
                                                        :body invalid-job-input)]
    (is (= 400 (:status response)))
    (is (= job-validation-output (:body response)))))

(deftest get-jobs-test
  (save-entity job-requests (json/read-str valid-job-request-input))
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/job-requests")]
    (is (= 200 (:status response)))
    (is (= (json/write-str @job-requests) (:body response)))))

(deftest get-jobs-not-found-test
  (let [response (pedestal-test/response-for service-fn 
                  :get "/api/v1/job-requests")]
    (is (= 404 (:status response)))
    (is (= "[]" (:body response)))))

(deftest create-job-request-test
  (save-data)
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/job-requests" 
                                                        :headers {"Content-Type" "application/json"}
                                                        :body valid-job-request-input)]
    (is (= 201 (:status response)))
    (is (= (json/write-str {"job_id" "c0033410-981c-428a-954a-35dec05ef1d2",
                            "agent_id" "8ab86c18-3fae-4804-bfd9-c3d6e8f66260"})
           (:body response)))))

(deftest create-job-request-failed-test
  (let [response (pedestal-test/response-for service-fn :post "/api/v1/job-requests" 
                                                        :headers {"Content-Type" "application/json"}
                                                        :body invalid-job-request-input)]
    (is (= 400 (:status response)))
    (is (= job-request-validation-output (:body response)))))

(deftest get-queue-state-waiting-test
  (save-data)
  (let [response (pedestal-test/response-for service-fn :get "/api/v1/jobs-queue" 
                                                        :headers {"Content-Type" "application/json"}
                                                        :body valid-job-request-input)]
    (is (= 200 (:status response)))
    (is (= (json/write-str 
      {"waiting" @jobs
       "on_progress" @jobs-on-progress 
       "finished" @finished-jobs})
      (:body response)))))
