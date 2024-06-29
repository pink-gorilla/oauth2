(ns demo.repl.token-info
  (:require
   [modular.system]
   [token.oauth2.store :refer [load-token]]
   [token.info :refer [decode-jwt user-email]]
   [token.util.base64 :refer [base64-decode]]))

(def this (modular.system/system :token-store))

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



(-> (load-token this :xero)
    (decode-jwt :id-token)
    :header
    :kid
    )


  (analyze-token this :xero :id-token)
  (analyze-token this :xero :access-token)
  (analyze-token this :xero :refresh-token)


  (analyze-token this :google :id-token)
  (analyze-token this :google :access-token)
  (analyze-token this :google :refresh-token)

  (analyze-token this :github :id-token)
  (analyze-token this :github :access-token)
  (analyze-token this :github :refresh-token)

  (analyze-token this :shiphero :id-token)
  (analyze-token this :shiphero :access-token)
  (analyze-token this :shiphero :refresh-token)


  (println "base64 decoding: ")
  ;(encoded-data store :google :id-token )
  ;(encoded-data store  :xero :access-token)
(encoded-data this  :xero :id-token)

  

 :deps {buddy/buddy-auth {:mvn/version "3.0.1"}
 :tasks {:requires [[buddy.auth :as auth]]
         verify-token-github (println "github authenticated: "
                                      (auth/authenticated? {:identity "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"}))

         verify-token-xero (println "xero authenticated: "
                                    (auth/authenticated? {:identity "eyJhbGciOiJSUzI1NiIsImtpZCI6IjFDQUY4RTY2NzcyRDZEQzAyOEQ2NzI2RkQwMjYxNTgxNTcwRUZDMTkiLCJ0eXAiOiJKV1QiLCJ4NXQiOiJISy1PWm5jdGJjQW8xbkp2MENZVmdWY09fQmsifQ.eyJuYmYiOjE2MzcyOTAwMDAsImV4cCI6MTYzNzI5MTgwMCwiaXNzIjoiaHR0cHM6Ly9pZGVudGl0eS54ZXJvLmNvbSIsImF1ZCI6Imh0dHBzOi8vaWRlbnRpdHkueGVyby5jb20vcmVzb3VyY2VzIiwiY2xpZW50X2lkIjoiNkU0RDk1M0JBQUU5NDlEMUExQjM5OTMwN0FENThCOTQiLCJzdWIiOiJkODZhNTIyMThiODk1MDFiODE0ZmIyMDY1YjU5NzNlMSIsImF1dGhfdGltZSI6MTYzNzI4OTMwOCwieGVyb191c2VyaWQiOiIzYzczNjBjMC02MTk1LTQ2MmQtYjkxMy03NmNlOWM2NmNiYjgiLCJnbG9iYWxfc2Vzc2lvbl9pZCI6IjllMmRmMzA3YTc2ZDQ3MTc4MGY5YTBkNGYwMGE2YTVmIiwianRpIjoiMGMzNGJkNTQxZGZiM2U1ZTdmMzBkNTU0ZGIyZTUyMTIiLCJhdXRoZW50aWNhdGlvbl9ldmVudF9pZCI6IjliODVkM2NiLTIyMzMtNDg3OC05OTFkLWNhYTkzODcxY2NhMiIsInNjb3BlIjpbImVtYWlsIiwicHJvZmlsZSIsIm9wZW5pZCIsImFjY291bnRpbmcucmVwb3J0cy5yZWFkIiwiYWNjb3VudGluZy5zZXR0aW5ncyIsImFjY291bnRpbmcuYXR0YWNobWVudHMiLCJhY2NvdW50aW5nLnRyYW5zYWN0aW9ucyIsImFjY291bnRpbmcuam91cm5hbHMucmVhZCIsImFjY291bnRpbmcudHJhbnNhY3Rpb25zLnJlYWQiLCJhY2NvdW50aW5nLmNvbnRhY3RzIiwib2ZmbGluZV9hY2Nlc3MiXX0.RVISkllEz39OnpsdScQtUSZ29XeeMA8REJW3kWapQvE3BSFSgRqK-CI5bUYHbXb6QdFrjpbAUvPRi2F6dkQNUz9IuoFvT2mgh66Kqm16nUnDGYcmka7lfklvVbwYDkMi2XFrwfSyBgkVsFfksPgt7_g0KjlNlJYTNLJRQuYMQCK0WxU4v2sQ4fDF3Lgciryi7ARGehdFO7TJu29z7MGuPFIBs_aWbjU8ZAthQq8pip4GDl_dfQogI7VO6mkC8sm1yxTjkFZQ3bS9tCv5hW9LEbHEIHmK5C9SEOjIP4j7JTnzFGwf7qat8vSla0YtAOypW91ZGRulMpXmXiAXE2DWHw"}))

