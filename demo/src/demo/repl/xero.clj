(ns demo.repl.xero
  (:require
   [promesa.core :as p]
   [modular.system]
   [token.oauth2.request :refer [GET]]
   [token.oauth2.store :refer [get-auth-header]]))

(def ts (modular.system/system :token-store))

(keys ts)
(:path ts)
(:role ts)
(:clj ts) ; culprit for repl print error.
ts
;; => Error printing return value (StackOverflowError) at clojure.lang.RT/assoc (RT.java:827).

(get-auth-header ts :xero)

(def r
  (GET  "https://api.xero.com/connections"
    {:headers (get-auth-header ts :xero)}))

@r
;; => ({:id "869760d0-3d93-4ff9-a01f-0dfc40108e14", 
;  :authEventId "19348f97-033e-4551-b1ac-1ccf02caa3ab", 
;  :tenantId "791f3cb4-97b9-45f9-b5e6-7319cda87626", 
;  :tenantType "ORGANISATION", 
;  :tenantName "CRB Clean INC", 
;  :createdDateUtc "2022-01-07T14:51:35.5172610", 
;  :updatedDateUtc "2022-06-09T22:18:36.0378050"})

r

(p/reject! r (ex-info "error" {:a "asdf"}))

(p/pending? r)



