(ns token.oauth2.handler
  (:require
   [modular.webserver.middleware.api :refer [wrap-api-handler]]
   [modular.ws.service.middleware :refer [wrap-ws]]
   ;[token.oauth2.handler.start :refer [handler-oauth2-start]]
   [token.oauth2.handler.redirect :refer [handler-oauth2-redirect]]
   ;[token.oauth2.handler.token :refer [token-handler]]
   ;[token.oauth2.handler.save :refer [handler-oauth2-save]]
   ))

; wrapping not needed and fucks up the redirection

#_(def handler-oauth2-start-wrapped
    (-> handler-oauth2-start
        wrap-api-handler))

(def handler-oauth2-redirect-wrapped
  (-> handler-oauth2-redirect
      wrap-ws))

#_(def token-handler-wrapped
    (-> token-handler
        wrap-api-handler))

#_(def handler-oauth2-save-wrapped
    (-> handler-oauth2-save
        wrap-api-handler))


