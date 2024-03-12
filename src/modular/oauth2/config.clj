(ns modular.oauth2.config
  (:require 
   [modular.config :refer [get-in-config config-atom]]))

;; this config is here, so that we do not use modular.config in many different places.
;; this allows us to change config format easier

(defn token-path []
  (get-in-config [:oauth2 :token-path]))


(defn provider-config [config provider]
  (get-in config [:oauth2 provider]))


;; local auth (username/password)

(defn local-token-secret []
  (get-in-config [:oauth2 :local :client-secret]))

(defn user-list []
  (get-in-config [:users]))

;; use oauth2 tokens:

(defn token-prefix [provider]
  (get-in-config [:oauth2 provider :token-prefix]))


;; full config
;; TODO: needs to be refactored
;; used in: 
;; modular.oauth2.authorize.token-handler
;; modular.oauth2.token.refresh
;; modular.oauth2.authorize.start-handler
;; modular.oauth2.middleware

(defn get-config-server [server-profile-kw]
  (get-in-config [:oauth2 :server server-profile-kw]))


 (defn get-config-user
  ([]
   (get-config-user :default))
  ([user-id-kw]
   (get-in-config [:oauth2 :user user-id-kw])))


(defn entire-config []
  @config-atom)




(comment 
  (token-path)
  
  
 ; 
  )

