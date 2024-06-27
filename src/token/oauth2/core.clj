(ns token.oauth2.core
  (:require
   [clojure.string]
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [cemerick.url :refer [url url-encode]]
   [ajax.core :as ajax]
   [clj-service.core :refer [expose-functions]]
   [token.util.base64 :refer [base64-encode]]
   [token.oauth2.provider :refer [oauth2-authorize oauth2-code-to-token-uri]]
   [token.oauth2.provider.default] ; side effects (add default oauth2 providers)
   ))

(defn assert-provider [[id p]]
  (assert (keyword? id) "oauth2 provider key needs to be a keyword")
  (assert (map? p) "oauth2 provider needs to be a map")
  (assert (:client-id p) "oauth2 provider needs :client-id key")
  (assert (:client-secret p) "oauth2 provider needs :client-secret key")
  (assert (:token-prefix p) "oauth2 provider needs :token-prefix key")
  (assert (string? (:client-id p)) "oauth2 provider needs :client-id with type string")
  (assert (string? (:client-secret p)) "oauth2 provider needs :client-secret  with type string")
  (assert (string? (:token-prefix p)) "oauth2 provider needs :token-prefix with type string"))

(defn assert-providers [ps]
  (assert (map? ps) "oauth2 providers needs to be a map")
  ;(doall (map assert-provider ps))
  )

(defn start-oauth2-providers [{:keys [clj providers] :as this}]
  (info "starting oauth2-provider service..")
  (assert-providers providers)
  (info "starting oauth2-provider service.. provider config ok.")
  (expose-functions clj
                  {:name "token-oauth2"
                   :symbols ['token.oauth2.core/url-authorize
                             'token.oauth2.core/exchange-code-to-token]
                   :permission nil
                   :fixed-args [this]})
  (info "oauth2-provider service running..")
  this)

(defn get-provider-client-id [{:keys [providers] :as this} p]
  (get-in providers [p :client-id]))

(defn get-provider-client-secret [{:keys [providers] :as this} p]
  (get-in providers [p :client-secret]))

(defn- set-relative-path [current-url path]
  (-> (url current-url)
      (assoc :path path)
      (.toString)))

(defn- url-without-qp [url-str]
  (debug "url with qp: " url-str)
  (let [url-no-qp-str (-> (url url-str)
                          (assoc :query nil :anchor nil)
                          str)]
    (debug "url without qp: " url-no-qp-str)
    url-no-qp-str))

(defn- provider-uri [provider]
  (let [provider-name (name provider)]
    {:start-uri    (str "/api/oauth2/start/" provider-name)
     :redirect-uri (str "/api/oauth2/redirect/" provider-name)
     :landing-uri  (str "/api/oauth2/landing/" provider-name)}))


(defn- url-redirect [provider-kw current-url]
  (->> provider-kw
       provider-uri
       :redirect-uri
       (set-relative-path current-url)
       (url-without-qp)))

(defn- scope->string [scope]
  (let [scope (if (nil? scope) "" scope)
        scope (if (string? scope)
                scope
                (clojure.string/join " " scope))]
    scope))

(defn url-authorize [this provider current-url scope]
  (let [query {:redirect_uri  (url-redirect provider current-url)
               :client_id (get-provider-client-id this provider)
               :scope (scope->string scope)}
        {:keys [uri 
                query-params
                authorize-redirect-uri-name]} (oauth2-authorize {:provider provider})
        ;query {; If the value is code, launches a Basic authorization code flow, requiring a POST to the token endpoint to obtain the tokens.
        ;       ;  If the value is token id_token or id_token token, launches an Implicit flow
        ;       ;:response_type authorize-response-type ; response_type=token
        ;       ; state: sessionid
        ;       }
        query (if authorize-redirect-uri-name
                (rename-keys query {:redirect_uri authorize-redirect-uri-name})
                query)
       
        query (merge query-params query)]
    (info "authorize query params: " query-params)
    (infof "oauth2 for: %s authorize-uri: %s params: %s" provider uri (pr-str query))
    (-> (url uri)
        (assoc :query query)
        str
        ;url-encode
        )))



(defn auth-header-oauth-token [client-id client-secret]
  {"Authorization" (str "Basic " (base64-encode (str client-id ":" client-secret)))})

(defn exchange-code-to-token [this {:keys [provider code current-url]}]
  (let [p (promise)
        uri (oauth2-code-to-token-uri {:provider provider})
        client-id (get-provider-client-id this provider)
        client-secret (get-provider-client-secret this provider)]
    (info "getting token for provider " provider " code :" code)
    (ajax/POST uri ; "https://github.com/login/oauth/access_token"
      :headers (auth-header-oauth-token client-id client-secret)
      :params {:client_id	 client-id
               :client_secret client-secret
               :code code
               :grant_type "authorization_code" ; xero
               :redirect_uri (url-redirect provider current-url) ; The same redirect URI that was used when requesting the code
               }
          ; :format (ajax/json-request-format) ; {:keywords? true}
      :format (ajax/url-request-format) ; xero
      :timeout         5000                     ;; optional see API docs
      :response-format (ajax/json-response-format {:keywords? true})
      :handler (fn [res]
                 (info provider "/get-token success!")
                 (debug provider "/get-token success: " res)
                 (deliver p res))
      :error-handler (fn [res]
                       (error provider "/get-token error: " res)
                       (deliver p res)
                           ;(reject p res)
                       ))
    @p))