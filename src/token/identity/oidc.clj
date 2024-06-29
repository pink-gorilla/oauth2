(ns token.identity.oidc
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [modular.permission.user :refer [find-user-id-via-email]]
   [modular.permission.session :refer [set-user!]]
   [clj-service.executor :refer [*user* *session*]]
   [clj-service.core :refer [expose-functions]]
   [token.oauth2.provider :as p]
   [token.identity.oidc.util :as util]
   [token.info :refer [user-email]]))


(defn start-oidc-token [{:keys [permission clj] :as this}]
  (info "starting oidc-token login service..")
  (assert permission ":permission (permission service reference) missing")
  (when clj
    (info "exposing oidc-identity services via clj-service..")
    (expose-functions clj
                      {:name "token-oidc"
                       :symbols ['token.identity.oidc/login]
                       :permission nil
                       :fixed-args [this]}))
  (info "oidc-token login service running..")
  this)

;; OIDC login

(defn validate-token [jwt jwks alg]
  (try
    (util/validate-jwt jwt jwks alg)
    (catch Exception ex
      (error "token validate exception: " ex)
      false)))

(defn login
  [{:keys [permission] :as this} {:keys [provider token]}]
  (info "login/oauth2-oidc: token: " token " session: " *session*)
  (let [email (user-email token)
        jwks-url  (p/oauth2-jwks-uri {:provider provider})
        jwks (util/get-jwks jwks-url)
        alg {:alg :rs256}
        jwt (util/token->id-jwt token)
        token-valid? (validate-token jwt jwks alg)]
    (info "login/oauth2-oidc: token-valid: " token-valid?)
    (let [user (find-user-id-via-email permission email)]
      (if user
        (do (info "perfect! logging in user: " user)
            (set-user! permission *session* user) 
            {:user user :email email :provider provider})
        (do (error "oidc login token is valid, but there is no user for email: " email)
             {:error "no user for this email" :email email :provider provider})))))