(ns maestro.dao-test
  (:require [clojure.test :refer :all]
            [maestro.dao :refer :all]))


(def entities (atom #{}))

(def fake-entity {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864" "extra_info" "There is no extra info :("})

(deftest test-save-entity
  (save-entity entities fake-entity)
  (is (= (contains? @entities fake-entity) true)))

(deftest test-try-save-duplicated-entity
  (save-entity entities fake-entity)
  (save-entity entities fake-entity)
  (is (= (contains? @entities fake-entity) true))
  (is (= (count @entities) 1)))

(deftest test-get-entity-by-uuid
  (save-entity entities fake-entity)
  (let [entity (get-entity entities "690de6bc-163c-4345-bf6f-25dd0c58e864")]
    (is (= entity fake-entity))))

(deftest test-entity-not-found
  (save-entity entities fake-entity)
  (let [entity (get-entity entities "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")]
    (is (= entity nil))))
