(ns demo.repl.oidc
  (:require
   [modular.system]
   [token.oauth2.store :refer [load-token]]
   [token.info :refer [decode-jwt user-email]]
   ))

(def ts (modular.system/system :token-store))

 (-> (load-token ts :google)
    (user-email))
 ;; => "awb99@gmail.com"

(-> (load-token ts :xero)
    (user-email))
 ;; => "awb99@gmail.com"

 ;; github does not have identity tokens.
(-> (load-token ts :github)
    (user-email))
 ;; => nil