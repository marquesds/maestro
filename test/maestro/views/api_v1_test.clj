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
    (reset! nu-agents #{}))
  (test-fn))

(use-fixtures :each reset-data)

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

(def nu-agent-validation-output
  "{\"error\":\"Value does not match schema: {\\\"name\\\" (not (instance? java.lang.String 123456))}\"}")

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
