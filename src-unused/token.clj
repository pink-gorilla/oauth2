(ns token.oauth2.handler.token
  (:require
   [taoensso.timbre :as timbre :refer [debug info error]]
   [ring.util.response :as res]
   [ring.util.codec :as codec]
   [ajax.core :as ajax]
   [modular.oauth2.config :refer [entire-config]]
   [token.util.base64 :refer [base64-encode]]
   [modular.oauth2.provider :refer [full-provider-config]]))

; (codec/form-encode 

;(defn token-request [url params])

; xero:
;Content-Type: application/x-www-form-urlencoded

;The request body will need to contain the grant type (authorization_code), code and redirect_uri

(defn auth-header-basic [token]
  {"Authorization" (str "Basic " token)})


(defn token-handler [req]
  (debug "token handler: " req)
  (let [code (get-in req [:query-params "code"])
        provider (-> (get-in req [:query-params "provider"])
                     keyword)
        url-redirect (get-in req [:query-params "url-redirect"])
        
        provider-config (full-provider-config (entire-config) provider)
        {:keys [token-uri client-id client-secret]} provider-config
        p (exchange-code-to-token provider code)
        ]
    (res/response @p)
    ))

(comment
  (handler-github-redirect {})

  ;
  )
