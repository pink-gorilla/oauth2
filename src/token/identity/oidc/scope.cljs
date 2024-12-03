(ns token.identity.oidc.scope)

(def scopes {:github [;https://docs.github.com/en/developers/apps/scopes-for-oauth-apps
                      "user"
                       ;"user:email" ; this includes ONLY the user email
                      ;"gist"
                      ;"repo"
                      "openid"]
             :google [;https://developers.google.com/identity/protocols/oauth2/scopes
                      ;"offline_access" ; does not work!
                      "openid" ; The scope parameter must begin with the openid value and then include the profile value, the email value, or both.
                      "email" ; If the email scope value is present, the ID token includes email and email_verified claims.
                      "profile" ; he profile scope value is present, the ID token might (but is not guaranteed to) include the user's default profile claims.
                      "https://www.googleapis.com/auth/userinfo.email"]
             :xero ["offline_access" ; refresh_token
                    "openid"
                    "profile"
                    "email"]})

(defn get-identity-scope [provider]
  (get scopes provider))