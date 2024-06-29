(ns demo.repl.identity.oidc
  (:require
   ;[buddy.auth :as auth]
   ;[token.util.base64 :refer [base64-decode]]
   [buddy.sign.jwt :as jwt]
   [buddy.core.keys :as keys]
   [token.identity.oidc.util
    :refer
    [token->id-jwt decode-jwt
     extract-kid
     get-jwks
     find-kid
     build-pem
     validate-jwt
     determine-algo]]
   ;[token.info :as token-info]
   [token.oauth2.store :refer [load-token]]
   [modular.system]))

*ns*


(def this (modular.system/system :token-store))

(def token (load-token this :google))

token

(def jwt (token->id-jwt token))

jwt


(def jwt-decoded  (decode-jwt jwt))

jwt-decoded

(extract-kid  jwt-decoded)

;; => "2af90e87be140c20038898a6efa11283dab6031d"


(def jwks-url "https://www.googleapis.com/oauth2/v3/certs")

(def jwks
  (get-jwks jwks-url))

jwks

(find-kid jwks "3d580f0af7ace698a0cee7f230bca594dc6dbb55")

(def pem
  (build-pem jwks jwt-decoded))

pem

(def public-key (keys/jwk->public-key pem))


(keys/public-key? public-key)

(determine-algo jwt-decoded)
;; => "RS256"


(jwt/unsign jwt public-key)

jwt

public-key

(jwt/unsign jwt public-key {:alg (keyword alg)})

(jwt/unsign jwt public-key {:alg :rs256})

(validate-jwt jwt jwks "RS256")
