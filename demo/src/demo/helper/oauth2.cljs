(ns demo.helper.oauth2
  (:require
   [re-frame.core :as rf]))

(rf/reg-event-db
 :oauth2/logged-in
 (fn [db [_ provider]]
   (println "***************** logged in to: " provider)))
