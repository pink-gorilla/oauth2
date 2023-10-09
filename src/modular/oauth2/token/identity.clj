(ns modular.oauth2.token.identity
  (:require
   [clj-jwt.core :refer [str->jwt]]))

(defn user-id [token]
  (let [id-token (:id-token token)
        id (when id-token
             (-> id-token
                 str->jwt
                 :claims
                 :email))]
    id))


(comment
  (require '[modular.oauth2.token.store :refer [load-token]])
    ; providers that have oidc
  (-> (load-token :xero) user-id)
  (-> (load-token :google) user-id)
    ; providers that do NOT have oidc
  (-> (load-token :github) user-id)

;  
  )