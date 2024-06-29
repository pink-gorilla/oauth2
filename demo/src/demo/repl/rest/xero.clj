(ns demo.repl.rest.xero
  (:require
   [modular.system]
   [token.oauth2.request :refer [GET]]
   [token.oauth2.core :refer [get-auth-header]]))

(def t (modular.system/system :oauth2))

@(get-auth-header t :xero)

@(GET "https://api.xero.com/connections"
     {:headers @(get-auth-header t :xero)})

;; => ({:id "869760d0-3d93-4ff9-a01f-0dfc40108e14", 
;  :authEventId "19348f97-033e-4551-b1ac-1ccf02caa3ab", 
;  :tenantId "791f3cb4-97b9-45f9-b5e6-7319cda87626", 
;  :tenantType "ORGANISATION", 
;  :tenantName "CRB Clean INC", 
;  :createdDateUtc "2022-01-07T14:51:35.5172610", 
;  :updatedDateUtc "2022-06-09T22:18:36.0378050"})