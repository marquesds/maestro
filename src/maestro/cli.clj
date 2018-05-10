(ns maestro.cli
  (:gen-class)
  (:require [clojure.data.json :as json]))

(defn parse-file! [file-path]
  (let [file-content (slurp file-path)]
    (json/read-str file-content)))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
