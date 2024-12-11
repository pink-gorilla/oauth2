(ns  token.oauth2.provider.woo
  (:require
   [token.oauth2.provider :refer [oauth2-flow-opts
                                  oauth2-flow-response-parse
                                  oauth2-auth-header-prefix]]))

; https://woocommerce.github.io/woocommerce-rest-api-docs/#rest-api-keys

#_(defn parse-authorize-token-response [{:keys [anchor]}]
    ; #access_token=ya29.a0ARrdaM9mY4gaGPSU_5pMhS7x3wsgrPhDWhGy0fQVIwlsz7soPBlLVnAAEYQWl9SudGnfmapQ_2dq1oa6jS-SlJlR59cniSm1TAFkrK2KEqmBnvJHNI-mux6GDFtuVh-st5eysR97Z3xHSfjkxhsf9QknOZLv
    ;  &token_type=Bearer
    ;  &expires_in=3599
    ;  &scope=email%20https://www.googleapis.com/auth/calendar%20https://www.googleapis.com/auth/drive.metadata%20https://www.googleapis.com/auth/docs%20https://www.googleapis.com/auth/drive%20https://www.googleapis.com/auth/drive.appdata%20openid%20https://www.googleapis.com/auth/drive.file%20https://www.googleapis.com/auth/cloud-platform%20https://www.googleapis.com/auth/drive.metadata.readonly%20https://www.googleapis.com/auth/spreadsheets.readonly%20https://www.googleapis.com/auth/userinfo.email%20https://www.googleapis.com/auth/drive.readonly%20https://www.googleapis.com/auth/spreadsheets%20https://www.googleapis.com/auth/gmail.readonly%20https://www.googleapis.com/auth/drive.photos.readonly&authuser=0&prompt=none
    (let [{:keys [access_token scope expires_in token_type]} anchor]
      {:access-token access_token
       :scope scope
       ;:expires #?(:cljs (js/parseInt expires_in)
       ;            :clj (:expires_in anchor))
       :type token_type}))

(defn user-parse [data]
  {:user (:id data)
   :email (:email data)})

(defmethod oauth2-flow-opts :woo [_]
  {; authorize
   :uri "https://www.crbclean.com/wc-auth/v1/authorize"
   :query-params {:response_type "code" ; "token" ; 
                  :access_type "offline"; the client does not receive a refresh token unless a value of offline is specified. (online or offline
                  :authorize-redirect-uri-name :return_url ; URL the user will be redirected to after authentication
                  }})

(defmethod oauth2-auth-header-prefix :woo [_]
  "Bearer")

(defmethod oauth2-flow-response-parse :woo [{:keys [query]}]
  (let [{:keys [scope code prompt authuser]} query]
    {:scope scope
     :code code}))

(def config
  {; access token
   :token-uri "https://www.googleapis.com/oauth2/v4/token"
   ;"https://accounts.google.com/o/oauth2/v2/access_token"
   :accessTokenResponseKey "id_token"

   ; api requests
   :auth-header api-request-auth-header
   ; userinfo
   :user nil
   :user-parse user-parse
   :icon "fab fa-google-plus"})

; https://woocommerce.github.io/woocommerce-rest-api-docs/
