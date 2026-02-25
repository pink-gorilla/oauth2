(ns token.identity.handler.me)


(defn me-handler [{:keys [identity] :as req}]
   #_{:user "florian"
      :roles ["logistic"]
      :email ["hoertlehner@gmail.com"]
      :provider "local"}
  (if identity
    {:status 200 :body identity}
    {:status 200 :body {:user nil :permissions nil}}))