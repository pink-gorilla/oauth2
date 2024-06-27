(ns demo.token-info
  (:require
   [token.store :refer [load-token]]
   [token.info :refer [decode-jwt user-id]]
   [token.base64 :refer [base64-decode]]))

#_{:alg "RS256"
   :kid "1CAF8E66772D6DC028D6726FD0261581570EFC19"
   :typ "JWT"
   :x5t "HK-OZnctbcAo1nJv0CYVgVcO_Bk"}

 ; (show-claims id :access-token)
#_{:aud "https://identity.xero.com/resources"
   :sub "d86a52218b89501b814fb2065b5973e1"
   :iss "https://identity.xero.com"
   :exp 1637518336
   :scope ["email" "profile" "openid" "accounting.reports.read" "accounting.settings" "accounting.attachments" "accounting.transactions" "accounting.journals.read" "accounting.transactions.read" "accounting.contacts" "offline_access"]
   :xero_userid "3c7360c0-6195-462d-b913-76ce9c66cbb8"
   :auth_time 1637516530
   :jti "2e68c7df3f7e30f84ac45c2c30aadbb7"
   :nbf 1637516536
   :global_session_id "c735d534d0204f4da4f8100c1437fe3e"
   :authentication_event_id "894f5c8c-3938-416d-966e-8f28fa24d358"
   :client_id "6E4D953BAAE949D1A1B399307AD58B94"}


(defn analyze-token [this id key]
  (let [token (load-token this id)
        decoded (when token
                  (-> (decode-jwt token key)
                      (dissoc :signature)))]
    (if decoded
      (println "\r\n \r\n " id  " / " key " data: " (pr-str decoded))
      (println "\r\n \r\n " id  " / " key " - NO DATA."))))


(defn encoded-data [this id key]
  (let [token (load-token this id)
        decoded (when token
                  (try
                    (-> (decode-jwt token key)
                        :encoded-data
                        (base64-decode))
                    (catch Exception ex
                      "")))]
    (println "\r\n \r\n " id  " / " key " encoded data: " (pr-str decoded))))






(defn run [store]

  (analyze-token store :xero :id-token)
  (analyze-token store :xero :access-token)
  (analyze-token store :xero :refresh-token)

  (analyze-token store :google :id-token)
  (analyze-token store :google :access-token)
  (analyze-token store :google :refresh-token)

  (analyze-token store :github :id-token)
  (analyze-token store :github :access-token)
  (analyze-token store :github :refresh-token)

  (analyze-token store :shiphero :id-token)
  (analyze-token store :shiphero :access-token)
  (analyze-token store :shiphero :refresh-token)


  (println "base64 decoding: ")
  ;(encoded-data store :google :id-token )
  ;(encoded-data store  :xero :access-token)
(encoded-data store  :xero :id-token)

  
  ;
  )
