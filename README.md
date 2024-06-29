# oauth2 [![GitHub Actions status |pink-gorilla/oauth2](https://github.com/pink-gorilla/oauth2/workflows/CI/badge.svg)](https://github.com/pink-gorilla/oauth2/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/oauth2.svg)](https://clojars.org/org.pinkgorilla/oauth2)


## core features

- oauth2 access-token engine
  - allow users to authorize the app via web interface from oauth2 providers (google, github, xero, ..)
  - get access-tokens for use with rest/graphql apis 
  - transparently renews access-tokens
  - token store (rudimentary but necessary)
  - its primary use is to let your application use oauth2 protected services.
- identity engine (to allow users to "login")
  - the idea is that you can expose clj services that are only accessible to certain users.
  - local-identity tokens via user/password
  - oidc tokens (using oauth2 flow)



# configuration

- [creds-empty.edn](https://github.com/pink-gorilla/oauth2/blob/main/creds-empty.edn) has empty values you can set and a description how to get the credentials
- before starting the demo you need to set the correct credentials.
- edit demo/deps.edn in :run alias set the :config to a file that contains your credentials.


# demo

```
cd demo
clj -X:webly:npm-install
clj -X:webly:compile
clj -X:webly:run
```

Open a webbrowser on port 8080

```

## gorilla dependencies

- permission (define users and their permissions)
- clj-service (expose clj functions via websocket and ring-handler)
- modular (edn persistence)


# how to use tokens (for example in rest api):

Have a look at [pink-gorilla/rest](https://github.com/pink-gorilla/rest) for rest-apis that use oauth2 tokens generated via this library.

