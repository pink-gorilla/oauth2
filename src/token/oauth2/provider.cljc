(ns token.oauth2.provider)

(defmulti oauth2-flow-opts
  "returns a map with provider-specific settings that are
   needed to generate the oauth2-authorize-url"
  :provider)

(defmulti oauth2-flow-response-parse
  ""
  :provider)

(defmulti oauth2-token-uri
  "the provider-specific endpoint uri that is used to 
   get a token or to refresh a token."
  :provider)

(defmulti oauth2-auth-header-prefix
  "the Authorization header for a rest get/put has a *prefix* that is
   provider specific (typically token or Bearer). 
   This multimethod returns just this string. 
   Input needs to be {:provider :provider-id}"
  :provider)

(defmulti oauth2-openid-uri
  "the provider-specific endpoint uri that is used to 
   get openid config."
  :provider)


(defmulti oauth2-jwks-uri
  "the provider-specific endpoint uri that is used to 
   get certificates for oidc jwks for identity verification."
  :provider)




(defmulti user-info-map
  ""
  :provider)