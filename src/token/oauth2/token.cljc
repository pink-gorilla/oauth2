(ns token.oauth2.token
  (:require
   [clojure.set :refer [rename-keys]]
   #?(:clj  [token.util.date :refer [now-instant add-seconds add-minutes]])
   #?(:clj [tick.core :as t])))

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

#?(:clj
(defn access-token-needs-refresh? [token]
  ;; todo: now needs to be UTC - for xero this is important, 
  ;; because xero only has 30 minutes valid auth tokens.
    (let [{:keys [expires-date]} token]
      (when expires-date
        (let [now (now-instant)
              now-p1 (add-minutes now 10)]
          (t/> now-p1 expires-date)))))
 ;  
)