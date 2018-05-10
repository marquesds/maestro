(ns maestro.dao)


(defn save-entity [collection entity]
  (do (swap! collection conj entity)
      collection))

(defn save-entity-keep-order [collection entity]
  "Expects an atom with clojure.lang.PersistentVector as collection"
  (if-not (.contains @collection entity)
    (save-entity collection entity)))

(defn get-entity-by-id [collection id]
  (first (filter (fn [dict] (= (get dict "id") id)) @collection)))
