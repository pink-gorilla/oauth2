(ns token.info
  (:require
   [token.oauth2.store :refer [load-token]]
   [buddy.sign.jwt :as jwt]
   [clj-jwt.core :refer [str->jwt]]
   ;[no.nsd.clj-jwt :as clj-jwt]
   ))

(defn decode-jwt
  "decodes jwt data (:key token )
   returns nil if decoding is not possible"
  [token key]
  (let [encoded (get token key)]
    (when encoded
      (try 
         (str->jwt encoded)  
        (catch Exception _
          nil)))))

; providers:
; [:xero :shiphero :google :github]

; keys:
; :id-token
; :access-token
; :refresh-token

; encoded keys:
; :header
; :claims
; :signature
; :encoded-data

(defn user-email
  "jwt oidc providers (such as :xero and :google)
   provide user identiy in the :id-token key.
   not all providers support oidc (example: github)
   returns the users email, or nil."
  [token]
  (let [decoded (decode-jwt token :id-token)]
    (when decoded
      (-> decoded :claims :email))))