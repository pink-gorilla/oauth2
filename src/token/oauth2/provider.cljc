(ns token.oauth2.provider)

(defmulti oauth2-authorize 
  "returns a map with provider-specific settings that are
   needed to generate the oauth2-authorize-url"
  :provider)

(defmulti oauth2-auth-header 
  "creates an auth-header for a given {:provider :token} 
   auth-header is a map with key Autorization and the value
   is what differs from provider to provider"
  :provider)

(defmulti oauth2-auth-response-parse
  ""
  :provider)


(defmulti oauth2-code-to-token-uri
  ""
  :provider)


(defmulti user-info-map
  ""
  :provider)