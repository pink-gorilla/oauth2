(ns modular.oauth2.config
  (:require 
   [modular.config :refer [get-in-config config-atom]]))



(defn token-path []
  (get-in-config [:oauth2 :token-path]))


(defn provider-config [config provider]
  (get-in config [:oauth2 provider]))


(comment 
  (token-path)
  
  
 ; 
  )

