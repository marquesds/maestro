(ns maestro.cli-test
  (:require [clojure.test :refer :all]
            [maestro.cli :refer :all]))

(def fake-file-content "[{\"new_agent\" {\"name\" \"BoJack Horseman\"}}]")

(deftest test-parse-json-file
  (with-redefs [slurp (fn [f & opts] fake-file-content)]
    (is (= [{"new_agent" {"name" "BoJack Horseman"}}] (parse-file "input.json")))))
