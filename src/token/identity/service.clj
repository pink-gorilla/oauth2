(ns token.identity.service
  (:require 
    [token.identity.local :refer [start-local-identity]]
    [token.identity.oidc :refer [start-oidc-identity]]))


  
(defn start-identity-service [{:keys [permission clj secret]}]
  {:local (start-local-identity
           {:permission permission
            :clj clj
            :secret secret})
   :oidc (start-oidc-identity
          {:permission permission
           :clj clj})})