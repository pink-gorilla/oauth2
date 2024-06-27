(ns token.oauth2.provider.xero
  (:require
   [token.oauth2.provider :refer [oauth2-authorize 
                                  oauth2-auth-header 
                                  oauth2-auth-response-parse
                                  user-info-map]]))

(defmethod oauth2-authorize :xero [_]
  {; authorize
   :uri "https://login.xero.com/identity/connect/authorize"
   :query-params {:response_type "code"
                  :returnUrl "https://login.xero.com/identity/identity/connect/authorize" ; not sure why this is needed.    
                  }})

(defmethod oauth2-auth-response-parse :xero [{:keys [query]}]
  ;# :query {:scope "email+https://www.googleapis.com/auth/drive.metadata.readonly+https://www.googleapis.com/auth/drive.appdata+openid+https://www.googleapis.com/auth/drive.file+https://www.googleapis.com/auth/drive.metadata+https://www.googleapis.com/auth/drive+https://www.googleapis.com/auth/userinfo.email+https://www.googleapis.com/auth/drive.readonly+https://www.googleapis.com/auth/gmail.readonly+https://www.googleapis.com/auth/drive.photos.readonly+https://www.googleapis.com/auth/spreadsheets+https://www.googleapis.com/auth/cloud_search+https://www.googleapis.com/auth/spreadsheets.readonly+https://www.googleapis.com/auth/calendar+https://www.googleapis.com/auth/cloud-platform+https://www.googleapis.com/auth/docs", 
  ;          :prompt "none", 
  ;          :authuser "0", 
  ;          :code "4/0AX4XfWh8wjf3vIem2f-1VTW5zMdppmmY3DSex9vVfsSANTmf2kDqtyBs049qTlXc0f54XQ"}, 
  ; :provider :google}
  (let [{:keys [scope code prompt authuser]} query]
    {:scope (:scope query)
     :code (:code query)}))


(defmethod oauth2-auth-header :xero [{:keys [token]}]
  {"Authorization" (str "Bearer " token)})

(defmethod user-info-map :xero [{:keys [token]}]
  {:uri  "https://api.xero.com/api.xro/2.0/Organisation"
   :parse-user-info-fn (fn [data]
                        {:user (get-in data [:Organisations :Name])
                         :email "no email"})})


(def config
  {; refresh token  
   :token-uri "https://identity.xero.com/connect/token"
   :accessTokenResponseKey "id_token"
})

;; Xero example for authorize request

; https://login.xero.com/identity/connect/authorize
; ?response_type=code
; &client_id=YOURCLIENTID
; &redirect_uri=YOURREDIRECTURI
; &scope=openid profile email accounting.transactions
; &state=123

; https://github.com/XeroAPI/Xero-OpenAPI

; https://xeroapi.github.io/xero-node/accounting/index.html
; https://github.com/XeroAPI/xero-node/blob/master/src/gen/api/accountingApi.ts

; var paginate = require("../paginate/paginate.js").paginate;

   ; https://xeroapi.github.io/xero-node/accounting/index.html#api-Accounting-getInvoice

   ; /Invoices/{InvoiceID}

; https://api-explorer.xero.com/

;; postman url:
;; https://app.getpostman.com/run-collection/d069793e904f7602770d#?env%5BOAuth%202.0%5D=W3sia2V5IjoiY2xpZW50X2lkIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6ImNsaWVudF9zZWNyZXQiLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoicmVmcmVzaF90b2tlbiIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJhY2Nlc3NfdG9rZW4iLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoieGVyby10ZW5hbnQtaWQiLCJ2YWx1ZSI6IiIsImVuYWJsZWQiOnRydWV9LHsia2V5IjoicmVfZGlyZWN0VVJJIiwidmFsdWUiOiIiLCJlbmFibGVkIjp0cnVlfSx7ImtleSI6InNjb3BlcyIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX0seyJrZXkiOiJzdGF0ZSIsInZhbHVlIjoiIiwiZW5hYmxlZCI6dHJ1ZX1d

(defn header-xero-tenant [tenant-id]
  {"Xero-Tenant-Id" tenant-id})

; request success:
;  "Status": "OK"