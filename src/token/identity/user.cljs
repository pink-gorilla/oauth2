(ns token.identity.user
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [reagent.core :as r]
   [token.identity.local :as local]
   [cljs.reader :refer [read-string]]))

;; LocalStorage Helpers

(defn ls-set! [k v]
  (.setItem js/localStorage (pr-str k) (pr-str v)))

(defn ls-get [k]
  (when-let [s (.getItem js/localStorage (pr-str k))]
    (read-string s)))

(defn ls-remove! [k]
  (.removeItem js/localStorage k))

(defonce user-key "oauth2-user")

(defonce user-a (r/atom nil))

(defn get-user []
  @user-a)

(defn set-user! [{:keys [token user] :as usermap}]
  (info "setting user to: " user)
  (reset! user-a usermap)
  (info "websocket login local-token: " token)
  (local/login token)
  (ls-set! user-key usermap))

(defn logout []
  (info "log out")
  (reset! user-a nil)
  (ls-remove! user-key))

(defn init-user!
  "fn to start user (defined in extension)"
  [_config]
  (info "initializing user ..")
  (when-let [usermap (ls-get user-key)]
    (info "user loaded from localstorage: " usermap)
    (reset! user-a usermap)))