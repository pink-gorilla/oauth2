(ns token.oauth2.core
  (:require
   [clojure.string]
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [cemerick.url :refer [url url-encode]]
   [token.oauth2.provider :refer [oauth2-authorize]]))

(defn assert-provider [p]
  (assert (map? p) "oauth2 provider needs to be a map")
  (assert (:client-id p) "oauth2 provider needs :client-id key")
  (assert (:client-secret p) "oauth2 provider needs :client-secret key")
  (assert (:token-prefix p) "oauth2 provider needs :token-prefix key")
  (assert (string? (:client-id p)) "oauth2 provider needs :client-id with type string")
  (assert (string? (:client-secret p)) "oauth2 provider needs :client-secret  with type string")
  (assert (string? (:token-prefix p)) "oauth2 provider needs :token-prefix with type string"))

(defn assert-providers [ps]
  (doall (map assert-provider ps)))

(defn start-oauth2-providers [{:keys [providers] :as this}]
  (assert-providers providers)

  this)

(defn get-provider-client-id [{:keys [providers] :as this} p]
  (-> @providers
      (get p)
      (get :client-id)))


(defn- set-relative-path [current-url path]
  (-> (url current-url)
      (assoc :path path)
      (.toString)))

(defn- url-without-qp [url-str]
  (info "url with qp: " url-str)
  (let [url-no-qp-str (-> (url url-str)
                          (assoc :query nil :anchor nil)
                          str)]
    (info "url without qp: " url-no-qp-str)
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
        query {; If the value is code, launches a Basic authorization code flow, requiring a POST to the token endpoint to obtain the tokens.
               ;  If the value is token id_token or id_token token, launches an Implicit flow
               ;:response_type authorize-response-type ; response_type=token

               ; state: sessionid
               }
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