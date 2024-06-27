(ns modular.oauth2.provider
  (:require
   [clojure.string]
   [clojure.set :refer [rename-keys]]
   [taoensso.timbre :as timbre :refer [debug info infof error]]
   [cemerick.url :refer [url url-encode]]
  
   [modular.oauth2.protocol :refer [provider-config known-providers]]
   [modular.oauth2.config :as config]))



(defn ring-oauth2-config [config]
  (let [provider-list (known-providers)
        list (map (fn [p] {p (full-provider-config config p)}) provider-list)]
    (apply merge list)))

;; AUTHORIZE - START

(defn url-start [provider-kw current-url]
  (let [provider-path (-> provider-kw provider-uri :start-uri)]
    (-> (url current-url)
        (assoc :query {:current-url current-url}
               :path provider-path)
        (str))))






;(defn url-without-qp [url-str]
;  (let [{:keys [proto host port path]} (url url-str)
;        port-str (if (< 0 port)
;                   (str ":" port)
;                   "")
;        url-str-no-qp (str proto ":" host port-str path)]
;    (info "url without qp: " url-str-no-qp)
;    url-str-no-qp))


;; REQUESTS (use the api)

(defn get-provider-auth-header [p token]
  (if-let [config (get-provider-config p)]
    (let [auth-header (:auth-header config)]
      (auth-header token))
    (do (error "cannot get auth header for unknwon provider")
        {})))

(defn parse-userinfo [p token]
  (if-let [config (get-provider-config p)]
    (let [user-parse (:user-parse config)]
      (user-parse token))
    (do (error "cannot parse userinfo unknwon provider")
        {})))