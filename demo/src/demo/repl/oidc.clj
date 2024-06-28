(ns demo.repl.oidc
  (:require
   [promesa.core :as p]
   [modular.system]
   [token.oauth2.store :refer [load-token]]
   [token.info :refer [decode-jwt user-id]]
   ))

(def ts (modular.system/system :token-store))

 (-> (load-token ts :google)
    (user-id))
 ;; => "awb99@gmail.com"

(-> (load-token ts :xero)
    (user-id))
 ;; => "awb99@gmail.com"

 ;; github does not have identity tokens.
(-> (load-token ts :github)
    (user-id))
 ;; => nil