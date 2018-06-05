(ns maestro.views.api-v1-test
  (:require [clojure.test :refer :all]
            [maestro.views.api-v1 :refer :all]
            [io.pedestal.http :as http]
            [io.pedestal.test :as pedestal-test]))

(defn reset-data 
  [test-fn]
  (do
    (reset! nu-agents #{})
    (reset! jobs #{})
    (reset! job-requests [])
    (reset! jobs-assigned #{}))
  (test-fn))

(use-fixtures :each reset-data)

(def service-fn
  (::http/service-fn (http/create-servlet {::http/routes routes ::http/port 9000})))

(deftest get-nu-agents-test
  (let [response (pedestal-test/response-for service-fn :get "/api/v1/nu-agents/1")]
  	(is (= "Hello, world!!!" (:body response)))))
