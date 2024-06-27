(ns token.store
  (:require
   [clojure.java.io :as io]
   [buddy.sign.jwt :as jwt]
   [modular.persist.protocol :refer [save loadr]]
   ))
  
(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn create-store [{:keys [path] :as this}]
  ;(println "token store: " path)
  (ensure-directory path)
  this)

(defn- filename-token  [{:keys [path] :as this} id]
  ;(println "token store path: " path)
  (str path "/" id ".edn"))

(defn save-token
  [{:keys [path] :as this} id data]
  (let [filename (filename-token this id)]
    (save :edn filename data)))

(defn load-token [this id]
  (let [filename (filename-token this id)]
    ;(println "loading token: " filename)
    (loadr :edn filename)))