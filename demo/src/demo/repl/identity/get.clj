(ns repl.identity.get
  (:require
   [token.oauth2.request :refer [GET]]))


@(GET  "https://identity.xero.com/.well-known/openid-configuration" {})

@(GET "https://accounts.google.com/.well-known/openid-configuration" {})

@(GET "https://www.googleapis.com/oauth2/v3/certs" {})

