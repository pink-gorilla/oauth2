(ns token.oauth2.provider)

(defmulti oauth2-authorize 
  "returns a map with provider-specific settings that are
   needed to generate thje oauth2-authorize-url"
  :provider)

(defmulti oauth2-auth-header 
  "creates an auth-header for a given :provider and :token
   input: {:provider :token} 
   returns the auth token"
  :provider)

(defmulti oauth2-auth-response-parse
  ""
  :provider)


(defmulti oauth2-code-to-token-uri
  ""
  :provider)