(ns maestro.schema
  (:require [schema.core :as s]))

(def nu-agent-schema
  {(s/required-key "id") s/Str
   (s/required-key "name") s/Str
   (s/required-key "primary_skillset") [s/Str]
   (s/required-key "secondary_skillset") [s/Str]})

(def job-schema
  {(s/required-key "id") s/Str
   (s/required-key "type") s/Str
   (s/required-key "urgent") s/Bool})

(def job-request-schema
  {(s/required-key "agent_id") s/Str})
