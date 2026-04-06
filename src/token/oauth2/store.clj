(ns token.oauth2.store
  (:require
   [babashka.fs :as fs]
   [ednx.fipp :refer [spit-fipp]]
   [ednx.edn :refer [slurp-edn]]
   [ednx.tick.edn :refer [add-tick-edn-handlers!]]
   [ednx.tick.fipp :refer [add-tick-fipp-printers!]]
   [token.oauth2.token :refer [sanitize-token]]))


(add-tick-edn-handlers!)
(add-tick-fipp-printers!)


(defn init-store [{:keys [store-path] :as this}]
  ;(println "token store: " path)
  (fs/create-dirs store-path)
  nil)

(defn- filename-token  [{:keys [store-path] :as _this} id]
  ;(println "token store path: " store-path)
  (str store-path "/" id ".edn"))

(defn save-token
  [{:keys [_store-path] :as this} id data]
  (let [filename (filename-token this id)
        data-safe (sanitize-token data)]
    (spit-fipp filename data-safe)))

(defn load-token [this id]
  (let [filename (filename-token this id)]
    ;(println "loading token: " filename)
    (slurp-edn filename)))

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

