(ns modular.oauth2.token.refresh
  (:require
   [tick.core :as t]
   [taoensso.timbre :as timbre :refer [debug info error]]
   [ajax.core :as ajax]
   [promesa.core :as p]
   [modular.oauth2.config :refer [entire-config]]
   ; [clojure.data.codec.base64 :as b64] ; perhaps alternative to modular.base-64
   [modular.oauth2.base64 :refer [base64-encode]]
   [modular.oauth2.provider :refer [full-provider-config]]
   [modular.oauth2.token.store :refer [load-token save-token]]
   [modular.oauth2.token.sanitize :refer [sanitize-token]]
   [modular.oauth2.date :refer [now-instant add-minutes]]
   ))
   
#_(defn auth-header-basic [token]
  {"Authorization" (str "Basic " token)})

(defn auth-header-oauth-token [client-id client-secret]
  {"Authorization" (str "Basic " (base64-encode (str client-id ":" client-secret)))})

(defn refresh-access-token [provider]
  (info "refreshing access token for: " provider)
  (let [token (load-token provider)
        refresh-token (:refresh-token token)
        r (p/deferred)
        provider-config (full-provider-config (entire-config) provider)
        {:keys [token-uri client-id client-secret]} provider-config
        header (auth-header-oauth-token client-id client-secret)
        params {:client_id	 client-id
                :client_secret client-secret
                :refresh_token refresh-token
                :grant_type "refresh_token"}]
    (info "refreshing token for provider " provider
          "client-id:" client-id "refresh token: " refresh-token
          "header: " header
          "params: " params)
    (ajax/POST token-uri
      :headers header
      ; grant_type=refresh_token
        ;refresh_token=Your refresh token
      :params params
      ; :format (ajax/json-request-format) ; {:keywords? true}
      :format (ajax/url-request-format) ; xero
      :timeout         5000                     ;; optional see API docs
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [res]
                 (info "refresh-token " provider "success!")
                 (debug provider "/refresh-token success: " res)
                 (let [token-new (sanitize-token res)
                       token-new (merge token token-new)
                       ; some providers include a new refresh token, some dont
                       ; so we merge the tokens so we use all fields from the new token,
                       ; and carry forward the old token (in case we do not get a new refresh-token)
                       ;(assoc token-new :refresh-token refresh-token)
                       ]
                   (debug "new access-token: " (:access-token token-new))
                   (save-token provider token-new)
                   (p/resolve! r token-new)))
      :error-handler (fn [res]
                       (error provider "/refresh-token error: " res)
                       (p/reject! r res)
                       ;(reject p res)
                       ))
    r))


;; todo: now needs to be UTC - for xero this is important, 
;; because xero only has 30 minutes valid auth tokens.

(defn access-token-needs-refresh? [kw-name]
  (let [{:keys [expires-date] :as token}  (load-token kw-name)
        now (now-instant)
        now-p1 (add-minutes now 10)]
    (if (and token expires-date)
      (do (info "now (+1 day): " now-p1 "token expiry:" expires-date)
          (t/> now-p1 expires-date))
      false)))




(comment
   (full-provider-config (entire-config) :xero)
  
    
   (def r (refresh-access-token :google))
   (p/resolved? r)
   @r
  
   (def r (refresh-access-token :xero))
   (p/resolved? r)
   @r
  
  (-> (load-token :xero) :expires-date type)
  


  (access-token-needs-refresh? :xero)
  (access-token-needs-refresh? :google)

  ;
  )
