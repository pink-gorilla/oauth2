(ns token.identity.oidc
  (:require
   [taoensso.timbre :refer [debug info warn] :as timbre]
   [modular.permission.user :refer [find-user-id-via-email get-user]]
   [modular.permission.session :refer [set-user!]]
   [clj-service.executor :refer [*user* *session*]]
   [clj-service.core :refer [expose-functions]]
   [token.oauth2.provider :as provider]
   [token.identity.oidc.util :as util]
   [token.identity.local :refer [create-claim]]))

(defn start-oidc-identity [{:keys [permission clj] :as this}]
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
    (warn "validate token: " jwt " jwks: " jwks " alg: " alg)
    (util/validate-jwt jwt jwks alg)
    (catch Exception ex
      (timbre/error "token validate exception: " ex)
      false)))

(defn login
  [{:keys [permission] :as this} {:keys [provider token]}]
  (info "login/oauth2-oidc: token: " token " session: " *session*)
  (let [;email (user-email token)
        jwks-url  (provider/oauth2-jwks-uri {:provider provider})
        _ (info "getting jwks for provider: " provider " url: " jwks-url)
        jwks (util/get-jwks jwks-url)
        alg {:alg :rs256}
        jwt (util/token->id-jwt token)
        _ (info "jwt token (access token): " jwt)
        {:keys [error email] :as validation-response} (validate-token jwt jwks alg)]
    (info "login/oauth2-oidc:validation-response: " validation-response)
    (if email
      (let [user-id (find-user-id-via-email permission email)]
        (if user-id
          (let [user (get-user permission user-id)
                claim (create-claim this {:type :oidc
                                          :provider provider
                                          :user (:id user)
                                          :roles (:roles user)
                                          :email (:email user)})]
            (info "perfect! logging in user: " user)
            (set-user! permission *session* user)
              ;{:user user :email email :provider provider}
            claim)
          (do (timbre/error "oidc login token is valid, but there is no user for email: " email)
              {:error "no user for this email" :email email :provider provider})))
      (do (timbre/error "provided oidc token is not valid!: " error)
          {:error "token is not valid!" :provider provider}))))