(ns token.oauth2.store
  (:require
   [babashka.fs :as fs]
   [modular.persist.protocol :refer [save loadr]]
   [modular.persist.edn] ; side effects to be able to save edn files
   [token.oauth2.token :refer [sanitize-token]]))

(defn init-store [{:keys [store-path] :as this}]
  ;(println "token store: " path)
  (fs/create-dirs store-path)
  nil)

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

