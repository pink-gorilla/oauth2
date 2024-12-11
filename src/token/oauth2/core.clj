(ns token.oauth2.core
  (:require
   [clojure.string]
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [cemerick.url :refer [url url-encode]]
   [ajax.core :as ajax]
   [promesa.core :as p]
   [clj-service.core :refer [expose-functions]]
   [token.util.base64 :refer [base64-encode]]
    ; [clojure.data.codec.base64 :as b64] ; perhaps alternative to modular.base-64
   [token.oauth2.provider :refer [oauth2-flow-opts oauth2-token-uri oauth2-auth-header-prefix]]
   [token.oauth2.provider.default] ; side effects (add default oauth2 providers)
   [token.oauth2.store :refer [load-token save-token]]
   [token.oauth2.token :refer [sanitize-token access-token-needs-refresh?]]))

(defn assert-provider [v]
  (let [[id p] v]
  (assert (keyword? id) (str "oauth2 provider key needs to be a keyword id: " id))
  (assert (map? p) "oauth2 provider needs to be a map")
  (assert (:client-id p) "oauth2 provider needs :client-id key")
  (assert (:client-secret p) "oauth2 provider needs :client-secret key")
  (assert (string? (:client-id p)) "oauth2 provider needs :client-id with type string")
  (assert (string? (:client-secret p)) "oauth2 provider needs :client-secret  with type string")))

(defn assert-providers [ps]
  (assert (map? ps) "oauth2 providers needs to be a map")
  (doall (map assert-provider ps)))                  

(defn start-oauth2-providers [{:keys [clj _store providers] :as this}]
  (info "starting oauth2-provider service..")
  (try
    (assert-providers providers)
    (catch AssertionError ex
      (info "assert error: " ex )
      (info "providers config: " providers)
      (throw (ex-info "oauth2 provider-config error!" {:ex ex}))))

  (info "starting oauth2-provider service.. provider config ok.")
  (expose-functions clj
                    {:name "token-oauth2"
                     :symbols ['token.oauth2.core/url-authorize
                               'token.oauth2.core/exchange-code-to-token]
                     :permission nil
                     :fixed-args [this]})
  (info "oauth2-provider service running..")
  nil)

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
                authorize-redirect-uri-name]} (oauth2-flow-opts {:provider provider})
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

(defn secure-post
  "makes a http-post request to a token endpoint. 
   this requests use client-id:client-secret authentication.
   returns a promesa promise"
  [this provider params]
  (let [r (p/deferred)
        uri (oauth2-token-uri {:provider provider})
        client-id (get-provider-client-id this provider)
        client-secret (get-provider-client-secret this provider)
        params (assoc params
                      :client_id	 client-id
                      :client_secret client-secret)]
    (info "secure-post provider: " provider " uri: " uri " params: " params)
    (ajax/POST uri
      {:headers (auth-header-oauth-token client-id client-secret)
       :params params
       :format (ajax/url-request-format)
       :timeout 5000
       :response-format (ajax/json-response-format {:keywords? true})
       :handler (fn [res]
                  (info "secure-post provider: " provider "url: " uri " success!")
                  (p/resolve! r res))
       :error-handler (fn [res]
                        (error  "secure-post provider: " provider "url: " uri " error: " res)
                        (p/reject! r (ex-info "http-get error!" res)))})
    r))

;; exchange code to token - this is used in the oauth2 auth flow

(defn exchange-code-to-token-request
  "makes a request to convert code to token. 
   returns promesa promise with the token."
  [this {:keys [provider code current-url]}]
  (let [params {:code code
                :grant_type "authorization_code" ; xero
                :redirect_uri (url-redirect provider current-url) ; The same redirect URI that was used when requesting the code
                }]
    (info "getting token for provider " provider " code :" code)
    (secure-post this provider params)))

(defn exchange-code-to-token [this opts]
  (let [r (exchange-code-to-token-request this opts)]
    @r))

;; refersh access token

(defn refresh-access-token-request [this provider refresh-token]
  (info "refreshing access token for: " provider)
  (let [params {:grant_type "refresh_token"
                :refresh_token refresh-token}]
    (info "refreshing access-token for provider " provider " refresh-token :" refresh-token)
    (secure-post this provider params)))

(defn reject! [p provider message]
  (p/reject! p (ex-info message
                        {:message message
                         :provider provider})))

(defn- reject-refresh! [p provider message]
  (error "cannot get refresh token for provider: " provider "reason: " message)
  (reject! p provider message))

(defn refresh-access-token [{:keys [store] :as this} provider]
  (let [r (p/deferred)
        token (load-token store provider)]
    (if-let [refresh-token (:refresh-token token)]
      (let [res (refresh-access-token-request this provider refresh-token)]
        #_{:access_token "ya29.a0AJTbYD4bTx2DEgQ0173"
           :expires_in 3599
           :scope " openid"
           :token_type "Bearer"
           :id_token "eyJhbZR-7arw"}
        (-> res
            (p/then (fn [token-new]
                      (info "new token: " token-new)
                      ; some providers include a new refresh token, some dont
                      ; so we merge the tokens so we use all fields from the new token,
                      ; and carry forward the old token (in case we do not get a new refresh-token)
                      ;(assoc token-new :refresh-token refresh-token)
                      (let [token-new (sanitize-token token-new)
                            token-new (merge token token-new)]
                        (info "new access-token: " (:access-token token-new))
                        (save-token store provider token-new)
                        (p/resolve! r token-new))))
            (p/catch (fn [err]
                       (error "refresh token for provider: " provider " exception: " err)
                       (reject-refresh! r provider "refresh-token error")))))
      (reject-refresh! r provider "no refresh token in store!"))
    r))

;; use access token

(defn- reject-access-token! [p provider message]
  (error "cannot get access-token for provider: " provider "reason: " message)
  (reject! p provider message))

(defn get-access-token
  "returns an access token for a provider.
   if access token is expired, it will be refreshed.
   returns a promesa promise"
  [{:keys [store] :as this} provider]
  (info "getting access token for provider: " provider)
  (let [r (p/deferred)
        {:keys [access-token] :as token} (load-token store provider)]
    (cond
      (nil? token)
      (reject-access-token! r provider "no token in store for provider")

      (nil? access-token)
      (reject-access-token! r provider "no access-token saved for provider")

      (access-token-needs-refresh? token)
      (-> (refresh-access-token this provider)
          (p/then (fn [{:keys [access-token]}]
                    (info "returning refreshed access token for provider: " provider " access-token:" access-token)
                    (p/resolve! r access-token)))
          (p/catch (fn [err]
                     (error "exception in refreshing access token: " err)
                     (reject-access-token! r provider "refresh access token went bad."))))
      :else
      (p/resolve! r access-token))
    r))

(defn- reject-header! [p provider message]
  (error "cannot get access-token for provider: " provider "reason: " message)
  (reject! p provider message))

(defn get-auth-header
  "auth header is used for rest requests as authentication.
   It is a map containing the key Authorization.
   returns a promesa promise."
  [this provider]
  (let [r (p/deferred)
        access-token-p (get-access-token this provider)]
    (-> access-token-p
        (p/then (fn [access-token]
                  (let [prefix (oauth2-auth-header-prefix {:provider provider})
                        headers {"Authorization" (str prefix " " access-token)}]
                    (info "auth header for provider: " provider " is: " headers)
                    (p/resolve! r headers))))
        (p/catch (fn [_err]
                   (reject-header! r provider "missing access token, cannot create auth-header"))))
    r))





