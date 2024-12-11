(ns token.identity.service
  (:require
   [token.identity.local :refer [start-local-identity]]
   [token.identity.oidc :refer [start-oidc-identity]]))

(defn start-identity-service [{:keys [permission clj secret]}]
  (let [this {:permission permission
              :clj clj
              :secret secret}]
  {:local (start-local-identity this)
   :oidc (start-oidc-identity this)}))