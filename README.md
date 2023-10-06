# oauth2 [![GitHub Actions status |pink-gorilla/oauth2](https://github.com/pink-gorilla/oauth2/workflows/CI/badge.svg)](https://github.com/pink-gorilla/oauth2/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/oauth2.svg)](https://clojars.org/org.pinkgorilla/oauth2)


## core features
- generate local-identity-token via user/password
- run oauth2 workflow to get tokens from oauth2 providers (google, github, woo-commerce, xero, ...)
- renew oauth2 tokens
- tokens can be used to access rest/graphql apis
- token store

## gorilla dependencies
- permission (define users and their permissions)
- websocket (oauth via websocket and ring-handler)
- modular (config, persistence)

## credentials setting
- The description how to get the credentials is in `demo-webly/creds-sample.edn`
- create `demo-webly/creds.edn`

# develop apps
