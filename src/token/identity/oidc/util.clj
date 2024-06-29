(ns token.identity.oidc.util
  (:require
   [buddy.core.keys :as keys]
   [buddy.sign.jwt :as jwt]
   [token.info :as token-info]
   [token.oauth2.request :refer [GET]]
   [clojure.string :as str]
   [clj-jwt.core :refer [str->jwt]]))

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
      (println "decoded jwt: " decoded-jwt)
      (println "pem: " pem)
      (println "public-key: " public-key)
      (when (keys/public-key? public-key)
        (jwt/unsign jwt public-key {:alg (keyword alg)})))
    (catch Exception e {:error (str "Error with public key: " (.getMessage e))})))


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




