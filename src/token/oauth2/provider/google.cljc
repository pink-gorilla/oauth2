(ns token.oauth2.provider.google
  (:require
   [token.oauth2.provider :refer [oauth2-flow-opts oauth2-flow-response-parse
                                  oauth2-token-uri
                                  oauth2-auth-header-prefix
                                  oauth2-openid-uri
                                  oauth2-jwks-uri
                                  user-info-map]]))

;; notes:
;; if browser already has authorized, google oauth2 dialog will not show any prompt,
;; but just get the access token. In this case there is no refresh-token being sent.
;; It could be that this is because of incremental-permissions. To be sure to get
;; a refresh token in this case, refresh the browser cache.

; https://oauth2.example.com/code?state=security_token%3D138r5719ru3e1%26url%3Dhttps%3A%2F%2Foa2cb.example.com%2FmyHome&code=4/
; https://developers.google.com/identity/protocols/oauth2/openid-connect)
; https://developers.google.com/identity/protocols/oauth2/openid-connect#createxsrftoken
; https://developers.google.com/identity/protocols/oauth2#5.-refresh-the-access-token,-if-necessary.
; https://developers.google.com/accounts/docs/OAuth2WebServer

#?(:cljs
   (defn nonce []
     (.toString (.random js/Math)))
   :clj
   (defn- nonce []
     (str (rand-int Integer/MAX_VALUE))))

(defmethod oauth2-flow-opts :google [_]
  {; authorize
   :uri "https://accounts.google.com/o/oauth2/v2/auth"
   :query-params {:response_type "code" ; "token" ; 
                  :access_type "offline"; the client does not receive a refresh token unless a value of offline is specified. (online or offline
                  :nonce (nonce)}})

(defmethod oauth2-auth-header-prefix :google [_]
  "Bearer")

(defmethod oauth2-flow-response-parse :google [{:keys [query]}]
  ;# :query {:scope "email+https://www.googleapis.com/auth/drive.metadata.readonly+https://www.googleapis.com/auth/drive.appdata+openid+https://www.googleapis.com/auth/drive.file+https://www.googleapis.com/auth/drive.metadata+https://www.googleapis.com/auth/drive+https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/drive.readonly+https://www.googleapis.com/auth/gmail.readonly+https://www.googleapis.com/auth/drive.photos.readonly+https://www.googleapis.com/auth/spreadsheets+https://www.googleapis.com/auth/cloud_search+https://www.googleapis.com/auth/spreadsheets.readonly+https://www.googleapis.com/auth/calendar+https://www.googleapis.com/auth/cloud-platform+https://www.googleapis.com/auth/docs", 
  ;          :prompt "none", 
  ;          :authuser "0", 
  ;          :code "4/0AX4XfWh8wjf3vIem2f-1VTW5zMdppmmY3DSex9vVfsSANTmf2kDqtyBs049qTlXc0f54XQ"}, 
  ; :provider :google}
  (let [{:keys [scope code prompt authuser]} query]
    {:scope scope
     :code code}))

(defmethod oauth2-token-uri :google [_]
  "https://www.googleapis.com/oauth2/v4/token")

(defmethod oauth2-openid-uri :google [_]
  "https://accounts.google.com/.well-known/openid-configuration")

(defmethod oauth2-jwks-uri :google [_]
  "https://www.googleapis.com/oauth2/v3/certs")

;(defn parse-authorize-token-response [{:keys [anchor]}]
    ; #access_token=ya29.a0ARrdaM9mY4gaGPSU_5pMhS7x3wsgrPhDWhGy0fQVIwlsz7soPBlLVnAAEYQWl9SudGnfmapQ_2dq1oa6jS-SlJlR59cniSm1TAFkrK2KEqmBnvJHNI-mux6GDFtuVh-st5eysR97Z3xHSfjkxhsf9QknOZLv
    ;  &token_type=Bearer
    ;  &expires_in=3599
    ;  &scope=email%20https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/drive.metadata%20https://www.googleapis.com/auth/docs%20https://www.googleapis.com/auth/drive%20https://www.googleapis.com/auth/drive.appdata%20openid%20https://www.googleapis.com/auth/drive.file%20https://www.googleapis.com/auth/cloud-platform%20https://www.googleapis.com/auth/drive.metadata.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly%20https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/gmail.readonly%20https://www.googleapis.com/auth/drive.photos.readonly&authuser=0&prompt=none
;    (let [{:keys [access_token scope expires_in token_type]} anchor]
 ;     {:access-token access_token
 ;      :scope scope
 ;      :expires #?(:cljs (js/parseInt expires_in)
 ;                  :clj (:expires_in anchor))
 ;      :type token_type}))

(defmethod user-info-map :google [{:keys [token]}]
  {:uri "https://www.googleapis.com/oauth2/v2/userinfo"
   :parse-user-info-fn (fn [data]
                         {:user (:id data)
                          :email (:email data)})})

(def config
  {;"https://accounts.google.com/o/oauth2/v2/access_token"
   :accessTokenResponseKey "id_token"
   :icon "fab fa-google-plus"})
