(ns modular.oauth2.config)

(def config
  (atom {:name "Webly" ; shown in popup-window
         }))
(defn app-name []
  (:name @config))