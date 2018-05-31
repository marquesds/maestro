(ns maestro.cli
  (:gen-class)
  (:require [clojure.data.json :as json]
  			    [clojure.tools.cli :refer [parse-opts]]
  			    [maestro.orchestrator :refer :all]))

(def cli-options
  [["-i" "--input PATH" "Input json"]])

(defn parse-file!
	[file-path]
	(let [file-content (slurp file-path)]
    	(lazy-seq (json/read-str file-content))))

(defn -main [& args]
  (let [options (select-keys (parse-opts args cli-options) [:options])]
  	(let [input-json-file-path (:input (:options options))]
  		(let [output (orchestrate (parse-file! input-json-file-path))]
  			(println (json/write-str output))))))
