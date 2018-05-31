(ns maestro.dao-test
  (:require [clojure.test :refer :all]
            [maestro.dao :refer :all]))

(def entities (atom #{}))
(def ordered-entities (atom []))

(defn reset-data 
  [test-fn]
  (do
    (reset! entities #{})
    (reset! ordered-entities []))
  (test-fn))

(use-fixtures :each reset-data)

(def fake-entity {"id" "690de6bc-163c-4345-bf6f-25dd0c58e864" "extra_info" "There is no extra info :("})
(def another-fake-entity {"id" "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88" "extra_info" "There is no extra info here too :("})

(deftest save-entity-test
  (save-entity entities fake-entity)
  (is (= (.contains @entities fake-entity) true)))

(deftest try-save-duplicated-entity-test
  (save-entity entities fake-entity)
  (save-entity entities fake-entity)
  (is (= (.contains @entities fake-entity) true))
  (is (= (count @entities) 1)))

(deftest try-save-nil-entity-test
  (save-entity entities nil)
  (is (empty? @entities)))

(deftest save-entity-keeping-insertion-order-test
  (save-entity-keep-order ordered-entities fake-entity)
  (save-entity-keep-order ordered-entities another-fake-entity)
  (is (= (.contains @ordered-entities fake-entity) true))
  (is (= (.contains @ordered-entities another-fake-entity) true))
  (is (= (conj [] fake-entity another-fake-entity) @ordered-entities)))

(deftest try-save-duplicated-entity-in-ordered-collection-test
  (save-entity-keep-order ordered-entities fake-entity)
  (save-entity-keep-order ordered-entities fake-entity)
  (is (= (.contains @ordered-entities fake-entity) true))
  (is (= (count @ordered-entities) 1)))

(deftest get-entity-by-uuid-test
  (save-entity entities fake-entity)
  (let [entity (get-entity-by-id entities "690de6bc-163c-4345-bf6f-25dd0c58e864")]
    (is (= entity fake-entity))))

(deftest entity-not-found-test
  (save-entity entities fake-entity)
  (let [entity (get-entity-by-id entities "ed0e23ef-6c2b-430c-9b90-cd4f1ff74c88")]
    (is (= entity nil))))

(deftest delete-entity-test
  (save-entity entities fake-entity)
  (save-entity entities another-fake-entity)
  (delete-entity entities fake-entity)
  (is (not (.contains @entities fake-entity)))
  (is (.contains @entities another-fake-entity)))

(deftest delete-entity-from-ordered-entities-test
  (save-entity ordered-entities fake-entity)
  (save-entity ordered-entities another-fake-entity)
  (delete-entity ordered-entities fake-entity)
  (is (not (.contains @ordered-entities fake-entity)))
  (is (.contains @ordered-entities another-fake-entity)))
