(ns token.identity.oidc.util
  (:require
   [taoensso.timbre :refer [debug info warn error]]
   [buddy.core.keys :as keys]
   [buddy.sign.jwt :as jwt]
   [token.info :as token-info]
   [token.oauth2.request :refer [GET]]
   [clojure.string :as str]
   [clj-jwt.core :refer [str->jwt]]))

; To verify the signature you should:
; Retrieve the public key by using the x5t or kid parameter.
; Break off the signature from the message leaving the header.payload encoded
; Convert the header+payload segment to an ASCII array
; Base64Url decode the signature
; Use the decoded signature to validate the header+payload ASCII byte array

; Use this Discovery endpoint to configure your application or API to automatically
; locate the JSON Web Key Set (JWKS) endpoint (jwks_uri), which contains the JWKS used
; to sign all Auth0-issued JSON Web Tokens (JWTs) signed with the RS256 signing algorithm. 
;The endpoint exists at:
; https://{yourDomain}/.well-known/openid-configuration.

(defn token->id-jwt [token]
  (:id-token token))

(defn decode-jwt
  "decodes jwt data 
   returns nil if decoding is not possible"
  [jwt]
  (when jwt
    (try
      (str->jwt jwt)
      (catch Exception _
        nil))))

(defn extract-kid [decoded-jwt]
  (-> decoded-jwt
      :header
      :kid))

(defn determine-algo [decoded-jwt]
  (-> decoded-jwt
      :header
      :alg))

; download public key from .well-known endpoing

(defn get-jwks
  [url]
  @(GET url {}))

(defn find-kid [jwks kid]
  (first (filter #(= (:kid %) kid) (:keys jwks))))

(defn build-pem
  [jwks jwt-decoded]
  (let [kid (extract-kid jwt-decoded)]
    (find-kid jwks kid)))

(defn validate-jwt
  [jwt jwks alg]
  (try
    (let [decoded-jwt (decode-jwt jwt)
          pem (build-pem jwks decoded-jwt)
          public-key (keys/jwk->public-key pem)]
      (info "decoded jwt: " decoded-jwt)
      (info "pem: " pem)
      (info "public-key: " public-key)
      (when (keys/public-key? public-key)
        (jwt/unsign jwt public-key alg)))
    (catch Exception e
      (error {:error (str "Error with public key: " (.getMessage e))}))))

#_(defn token->pem [token jwks-url]
    (let [;jwt (:id-token token)
         ;decoded-jwt (decode-jwt jwt)
          decoded-jwt (decode-id-token token)]
  ;(build-pem decoded-jwt jwks-url)
      decoded-jwt))

(defn validate-token [token jwks-url]
  (let [jwt (:id-token token)
        alg (determine-algo token)]
    (validate-jwt jwt jwks-url alg)))




