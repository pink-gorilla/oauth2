(ns demo.repl.oidc
  (:require
   [modular.system]
   [token.oauth2.store :refer [load-token]]
   [token.info :refer [decode-jwt user-email]]
   [token.oauth2.core :refer [url-start]]))

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


(def oidc (modular.system/system :token))

oidc


(url-start {:provider :google
            :current-url "http://localhost:8080/vionfi/ASD"
            :scope ["email"]
            :save-as "gmail-florian"})

; http://localhost:8080/token/oauth2/start/google?scope=email&state=gmail-florian

(url-start {:provider :google
            :current-url "http://localhost:8080/"
            :save-as "identity"})


; http://localhost:8080/token/oauth2/start/google?state=identity