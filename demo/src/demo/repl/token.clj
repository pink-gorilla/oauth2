 (ns demo.repl.token
   (:require
    [modular.system]
    [token.oauth2.core :refer [get-access-token 
                               refresh-access-token
                               get-auth-header
                               ]]))
  
 (def te (modular.system/system :oauth2))
 
;; refreshing access token is transparent.
;; but here we test refreshing the access tokens.

@(refresh-access-token te :google)
;; => {:scope "https://www.googleapis.com/auth/drive openid" 
;;     :access-token "ya29.atQ", 
;;     :token-type "Bearer", 
;;     :expires-in 3599, 
;;     :expires-date #inst "2024-06-28T20:07:48.058074142-00:00"}

@(refresh-access-token te :xero)
 
@(refresh-access-token te :github)
;; github does not use access-tokens that expire, for this 
;; 
;; => Execution error (ExceptionInfo) at token.oauth2.core/reject! (REPL:168).
;;    no refresh token in store!


;; gets a valid access token or fails (promesa promise reject)

@(get-access-token te :google)
@(get-access-token te :xero)
@(get-access-token te :github)

 
@(get-auth-header te :google)
@(get-auth-header te :xero)
@(get-auth-header te :github)



 
