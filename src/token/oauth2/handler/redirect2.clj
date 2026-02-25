(ns token.oauth2.handler.redirect2
  (:require
   [ring.util.response :as response]
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [token.oauth2.core :refer [exchange-code-to-token url-redirect]]
   ;[token.oauth2.provider :refer [oauth2-flow-response-parse]]
   [token.oauth2.token :refer [sanitize-token]]
   [token.oauth2.store :refer [save-token]]
   [token.identity.oidc :refer [login]]))

(defn redirect-url [{:keys [scheme server-name server-port uri] :as req} provider]
   ; {:scheme :http :server-name "localhost" :server-port 8080
   ;  :uri "/api/oauth2/start/github" :protocol "HTTP/1.1"}
   ; :headers {"host" "localhost:8080"}, 
  (info "req->url: " (select-keys req [:scheme :server-name :server-port :uri]))
  (str (name scheme) "://" server-name ":" server-port "/api/oauth2/redirect/" provider))

#_{:provider :google
   :anchor {},
   :query {:scope "email profile openid https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email",
           :prompt "consent",
           :authuser "0",
           :code "4/0ATx3LY4lnqT4ouMOPf7JIkIjFcXjnxu6Y6aL47n1J6ZcIF950eCI4WXmnI_rFXafYNuzAw"}}

#_{:iss "https://accounts.google.com",
   :code "4/0AfrIepDi5QXx2JRGoVAOtPtk7Ht5xXFo3wMZM6pM6qB7qqZaO1rLexL7C2CvBG7t5g3tng",
   :scope "email profile https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile openid",
   :authuser "0", :prompt "none"}

(defn handler-oauth2-redirect [{:keys [ctx params path-params] :as req}]
  (warn "oauth2/authorize-response: params: " params " path-params: " path-params)
  (let [this (:token ctx)
        {:keys [provider]} path-params
        provider-kw (keyword provider)
        {:keys [scope code state]} params
        url (redirect-url req provider-kw)]
    (warn "redirect this keys: " (keys this))
    (when code
      (info "exchanging code to token for " provider  " code: " code)
      (info "ctx keys: " (keys ctx) "this keys: " (keys this))
      (info "oauth2 providers: " (:providers this))
      (let [t (exchange-code-to-token this {:provider provider-kw
                                            :code code
                                            :current-url url
                                            :state state})
            _ (info "raw token: " t)
            t (sanitize-token t)
            _ (info "sanitized token: " t)]
        (info "state:" state)

        (save-token this provider-kw t)

        (if (= state "identity")
          (login this {:provider provider-kw
                       :id-token (:id-token t)})
          {:status 200
           :body (str "token with scope " scope " stored to " state)})))))

