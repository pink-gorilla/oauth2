(ns token.identity.user
  (:require
   [taoensso.timbre :refer-macros [info error]]
   [reagent.core :as r]
   [token.identity.local :as local]))

(defonce user-a (r/atom nil))

(defn get-user []
  @user-a)

(defn set-user! [{:keys [token user]}]
  (info "setting user to: " user)
  (reset! user-a user)
  (info "websocket login local-token: " token)
  (local/login token))

(defn ^:export init-user!
  [& _]
  (info "initializing user .."))