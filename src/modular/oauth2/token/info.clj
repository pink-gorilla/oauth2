(ns modular.oauth2.token.info
  (:require
   [modular.oauth2.token.store :refer [load-token]]
   [buddy.sign.jwt :as jwt]
   [clj-jwt.core :refer [str->jwt]]
   ;[no.nsd.clj-jwt :as clj-jwt]
   [clojure.pprint :refer [print-table]]
   [modular.oauth2.token.identity :refer [user-id]]))

(defn get-token [p kw]
  (-> (load-token p)
      kw ; :access-token
      str->jwt))

(defn get-header [p kw]
  (-> p (get-token kw) :header))

(defn get-claims [p kw]
  (-> p (get-token kw) :claims))

(defn get-signature [p kw]
  (-> p (get-token kw) :signature))

(defn get-encoded-data [p kw]
  (-> p (get-token kw) :encoded-data))

(defn token-summary [p]
  (let [token (load-token p)]
    {:provider p
     :available (if token true false)
     :id (user-id token)
     :expires-date (:expires-date token)}))

(defn tokens-summary [providers]
  (into []
        (map token-summary providers)))

(defn- token-summary-vec [p]
  [p (token-summary p)])

(defn tokens-summary-map [providers]
  (into {}
        (map token-summary-vec providers)))

(defn token-info-table [kw-name-seq]
  (let [token-infos (map token-summary kw-name-seq)]
    token-infos))

(defn print-token-table [kw-name-seq]
  (->> kw-name-seq
       (token-info-table)
       (print-table [:provider :available :id :expires-date])))

(comment
  (get-token :xero :id-token)
  (get-token :google :id-token)
  (get-token :github :id-token) ; no id-token

  (get-token :xero :access-token)
  (get-token :google :access-token) ; throws!
  (get-token :github :access-token) ; throws!

  (get-token :google :refresh-token) ; throws

  (print-token-table [:xero :shiphero :google :github])

; 
  )