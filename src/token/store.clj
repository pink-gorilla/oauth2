(ns token.store
  (:require
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [clojure.java.io :as io]
   [buddy.sign.jwt :as jwt]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side effects to be able to save edn files
   [clj-service.core :refer [expose-functions]]))

(defn- ensure-directory [path]
  (when-not (.exists (io/file path))
    (.mkdir (java.io.File. path))))

(defn create-store [{:keys [path clj role] :as this}]
  ;(println "token store: " path)
  (ensure-directory path)
  (when clj
    (info "exposing oauth2-store service permission: " role " .. ")
    (expose-functions clj
                      {:name "token-oauth2"
                       :symbols ['token.store/save-token
                                 'token.store/token-summary
                                 ;'token.oauth2.core/load-token
                                 ]
                       :permission role
                       :fixed-args [this]}))

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

; token summary

(defn token-summary-provider [this id]
  (let [token (load-token this id)]
    {:provider id
     :available (if token true false)
     :user id ; (user-id token)
     :expires-date (:expires-date token)}))

(defn token-summary [this providers]
  (->> providers
      (map (partial token-summary-provider this))
      (into [])))