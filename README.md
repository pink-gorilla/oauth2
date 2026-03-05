# token [![GitHub Actions status |pink-gorilla/token](https://github.com/pink-gorilla/token/workflows/CI/badge.svg)](https://github.com/pink-gorilla/token/actions?workflow=CI)[![Clojars Project](https://img.shields.io/clojars/v/org.pinkgorilla/token.svg)](https://clojars.org/org.pinkgorilla/token)


## core features

*identity engine* 
- the identity of the user gets verified via a token that gets decrypted/verified on the clj-server
- in the browser the token is stored as a cookie (best practice)
- middleware to enforce signed-in for static webpages
- cljs reagent helper functions to keep track of the user in the frontend
- clj-service uses token to manage access based on user roles
- local-identity tokens via user/password
- oidc2 identity tokens (using oauth2 flow)


*oauth2* access-token engine
- allow users to authorize the app via web interface from oauth2 providers (google, github, xero, ..)
- get access-tokens for use with rest/graphql apis 
- transparently renews access-tokens
- token store (rudimentary but necessary)
- its primary use is to let your application use oauth2 protected services.



# demo

- [creds-empty.edn](https://github.com/pink-gorilla/token/blob/main/creds-empty.edn) has empty values you can set and a description how to get the credentials
- before starting the demo you need to set the correct credentials.
- edit demo/deps.edn in :run alias set the :config to a file that contains your credentials.

```
cd demo
clj -X:npm-install
clj -X:demo
```

Open a webbrowser on port 8080

```



# how to use tokens (for example in rest api):

Have a look at [pink-gorilla/rest](https://github.com/pink-gorilla/rest) for rest-apis that use oauth2 tokens generated via this library.

