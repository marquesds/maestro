(ns maestro.views.schema
  (:gen-class)
  (:require [schema.core :as s]))

(def nu-agent-schema
  {(s/required-key "id") s/Str
   (s/required-key "name") s/Str
   (s/required-key "primary_skillset") [s/Str]
   (s/required-key "secondary_skillset") [s/Str]})
