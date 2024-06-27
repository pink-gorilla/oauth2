(ns token.sanitize
  (:require
   [clojure.set :refer [rename-keys]]
   #?(:clj  [modular.oauth2.date :refer [now-instant add-seconds]])))

#?(:clj

   (defn- add-expire-date [token]
     (if-let [expires-in (:expires-in token)]
       (assoc token :expires-date
              (-> (now-instant) (add-seconds expires-in)))
       token)))

(defn- rename [token]
  (rename-keys token
               {:access_token :access-token
                :refresh_token :refresh-token
                :id_token :id-token
                :token_type :token-type
                :expires_in :expires-in}))

(defn sanitize-token [token]
  (if (map? token)
    (-> token
        rename
        #?(:clj add-expire-date))

    token))


