(ns token.core
  (:require
   [taoensso.timbre :refer [info warn]]
   [modular.permission.core :refer [start-permissions]]
   [token.oauth2.store :refer [init-store]]
   [token.oauth2.core :refer [assert-providers]]
   [token.oauth2.provider.default] ; side effects (add default oauth2 providers)
   ))

(defn start-token-service [{:keys [users secret store-path providers auth-expiry]
                            :or {users {}
                                 secret "123456"
                                 store-path "/tmp"
                                 providers {}
                                 auth-expiry 3600}}]
  (info "token-service starting ..")
  (when (empty? users)
    (warn "token-service is starting with no users."))
  (when (= secret "123456")
    (warn "token-service is starting with default secret. signed tokens will not be secure"))
  (when (empty? providers)
    (warn "token-service is starting with no providers. oauth2 tokens will not work."))
  (let [this {:users (start-permissions users)
              :secret secret ; local identity
              :store-path store-path
              :providers providers
              :auth-expiry auth-expiry}]
    (assert-providers providers)
    (init-store this)
    (info "token-service started.")
    this))



