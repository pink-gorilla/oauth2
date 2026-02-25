(ns token.oauth2.handler.start
  (:require
   [taoensso.timbre :as timbre :refer [debug info warn error]]
   [clojure.string :as str]
   [ring.util.response :as response]
   [token.oauth2.core :refer [url-authorize]]
   [token.identity.oidc.scope :refer [get-identity-scope]]))

(defn redirect-url [{:keys [scheme server-name server-port uri] :as req} provider]
   ; {:scheme :http :server-name "localhost" :server-port 8080
   ;  :uri "/api/oauth2/start/github" :protocol "HTTP/1.1"}
   ; :headers {"host" "localhost:8080"}, 
  (info "req->url: " (select-keys req [:scheme :server-name :server-port :uri]))
  (str (name scheme) "://" server-name ":" server-port "/api/oauth2/redirect/" provider))

(defn handler-oauth2-start
  "this handler is opened in a browser-popup window.
   It redirects to the oauth2 start url of the provider"
  [{:keys [ctx path-params params] :as req}]
   ; :remote-addr :headers :server-port :http :uri :server-name :query-string :path-params  :scheme :request-method)
  (warn "oauth2/authorize-start: params:" params  " path-params:" path-params)
  (info "oauth2/authorize-start: req keys:" (keys req))
  (let [this (:token ctx)
        {:keys [provider]} path-params
        {:keys [scope state]} params
        provider-kw (keyword provider)
        redirect-url (redirect-url req provider)
        _ (info "redirect url: " redirect-url)
        _  (info "oauth2 start for provider: " provider-kw "url: " redirect-url)
        scope (if (or (nil? scope) 
                      (and (string? scope) (str/blank? scope)))
                (get-identity-scope provider-kw)
                scope)
        state (or state "identity")]
    (info "scope: " (pr-str scope))
    (info "state: " state)
    (-> (response/redirect (url-authorize this
                                          {:provider provider-kw
                                           :current-url redirect-url
                                           :scope scope
                                           :save-as state})))))

#_{:response_type "code", :access_type "offline", :nonce "376250065",
   :redirect_uri "http://localhost:8000/api/oauth2/redirect/google",
   :client_id "8777197473-d8qju1k2e2o1hklfouht1v0m36imgbrv.apps.googleusercontent.com",
   :scope "openid email profile https://www.googleapis.com/auth/userinfo.email"}
;