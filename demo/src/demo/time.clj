(ns demo.time
  (:require
   [modular.date :refer [now-str now  now-local]]
   [clj-service.executor :refer [*user* *session*]]))

(defn time-public []
  (now-str))

(defn time-user []
  (now))

(defn time-supervisor []
  (now-local))

(defn time-debug []
  (println "running time-debug user: " *user* " session: " *session*)
  {:user  *user*
   :session *session*})