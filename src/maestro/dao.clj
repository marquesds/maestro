(ns maestro.dao)


(defn save-entity [collection entity]
  (swap! collection conj entity))

(defn get-entity [collection uuid]
  (first (filter (fn [dict] (= (get dict "id") uuid)) @collection)))
