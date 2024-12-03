(ns token.local-test
  (:require
   [clojure.test :refer [deftest is testing]]
   [modular.permission.core :refer [start-permissions]]
   [token.identity.local :as local]))

(def permission-service
  (start-permissions
   {:demo {:roles #{:admin :logistic}
           :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
           :email ["john@doe.com"]}
    :awb99 {:roles #{:management :admin}
            :password "a231498f6c1f441aa98482ea0b224ffa" ; "1234"
            :email ["awb99@gmail.com"]}}))

(def this (local/start-local-token  {:permission permission-service
                                     :secret "AbHzj834ri9"}))

(deftest local-token-test []
  (let [{:keys [user token] :as t} (local/get-token this "demo" "1234")]
    (println "get-token result token: " (pr-str t))
    (is (= user :demo))
    (is (= {:user :demo
            :type "local"
            :provider "local"}
           (local/verify-token this token)))))

