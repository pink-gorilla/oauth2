(ns token.oauth2.store
  (:require
   [taoensso.timbre :as timbre :refer [info]]
   [babashka.fs :as fs]
   [clj-service.core :refer [expose-functions]]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side effects to be able to save edn files
   [token.oauth2.token :refer [sanitize-token]]))

(defn create-store [{:keys [store-path clj store-role] :as this}]
  ;(println "token store: " path)
  (fs/create-dirs store-path)
  (when clj
    (info "exposing oauth2-store service permission: " store-role " .. ")
    (expose-functions clj
                      {:name "token-oauth2"
                       :symbols ['token.oauth2.store/save-token
                                 'token.oauth2.store/token-summary
                                 ;'token.oauth2.core/load-token
                                 ]
                       :permission store-role
                       :fixed-args [this]}))

  this)

(defn- filename-token  [{:keys [store-path] :as this} id]
  ;(println "token store path: " store-path)
  (str store-path "/" id ".edn"))

(defn save-token
  [{:keys [store-path] :as this} id data]
  (let [filename (filename-token this id)
        data-safe (sanitize-token data)]
    (save :edn filename data-safe)))

(defn load-token [this id]
  (let [filename (filename-token this id)]
    ;(println "loading token: " filename)
    (loadr :edn filename)))

; token summary

(defn token-summary-provider [this id]
  (let [token (load-token this id)]
    {:provider id
     :available (if token true false)
     :user id ; (user-email token)
     :expires-date (:expires-date token)}))

(defn token-summary [this providers]
  (->> providers
       (map (partial token-summary-provider this))
       (into [])))

