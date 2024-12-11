(ns token.oauth2.service
  (:require
   [token.oauth2.store :refer [create-store]]
   [token.oauth2.core :refer [start-oauth2-providers]]))

(defn start-oauth2-service [{:keys [clj providers store-path store-role]}]
  (let [store (create-store {:clj clj
                             :store-path store-path
                             :store-role store-role})]
    (start-oauth2-providers {:clj clj
                             :store store
                             :providers providers})
    {:store store
     :providers providers}))


