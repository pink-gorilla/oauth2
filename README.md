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
- oauth2 and local-identity need a configuration before they work
- `creds-empty.edn` has empty values you can set and a description how to get the credentials


# demo

The demo uses the extension manager from goldly to add oauth2 to goldly.

```
cd demo
clj -X:demo:npm-install
clj -X:demo:compile
clj -X:demo
```

# demo - inspect received tokens
```
clj -X:run:token-info-google

```