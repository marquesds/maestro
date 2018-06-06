(ns maestro.dao)

(defn save-entity 
  [coll entity]
  (when-not (nil? entity)
    (swap! coll conj entity) 
    coll))

(defn save-entity-keep-order 
  [coll entity]
  "Expects an atom with clojure.lang.PersistentVector as coll"
  (when-not (.contains @coll entity)
  	(save-entity coll entity)))

(defn get-entity-by-id 
  ([coll id]
    (get-entity-by-id coll id "id"))
  ([coll id k]
    (first (filter (fn [dict] (= (get dict k) id)) @coll))))

(defn delete-entity
  [coll entity]
  (swap! coll (fn [x] (remove #{entity} x)))
    coll)
