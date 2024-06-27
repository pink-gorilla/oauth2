(ns modular.oauth2
  (:require
   #?(:clj  [taoensso.timbre :refer [debug info warn error]])
   #?(:cljs [taoensso.timbre :refer-macros [debug info warn error]])
   #?(:clj  [modular.oauth2.config :as config])
   #?(:clj  [modular.oauth2.local.handler])
   #?(:cljs [token.request])
   #?(:cljs [token.auth.ui.subscriptions])
   #?(:cljs [token.auth.start])
   #?(:cljs [token.auth.redirect-events])
   #?(:cljs [token.auth.userinfo])
   #?(:cljs [token.auth.code-2-token])
   #?(:cljs [token.login.login-dialog])))

#?(:clj
   (defn get-config-server [server-profile-kw]
     (config/get-config-server server-profile-kw)))

#?(:clj
   (defn get-config-user
     ([]
      (get-config-user :default))
     ([user-id-kw]
      (config/get-config-user user-id-kw)))

;
   )

; (nano-id 6)

(defn start-authorize-workflow [{:keys [provider scopes]}])

