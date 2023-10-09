

{:client-id "<APP_CLIENT_ID>"
 :client-secret "<APP_CLIENT_SECRET>"
 :api-role "757251779030" ; arn:aws:iam::757251779030:role/webly2
 :region "na" ; “eu”, “na” or “fe” =>Europe, North America, Far east
 }

; https://sellercentral.amazon.com/s
; https://sellingpartnerapi-na.amazon.com


; Amazon Vendor Central has a new, modern Selling Partner API (SP-API). The new API supports both sellers and vendors alike within a unified platform. Seller Central has had API access under the Amazon Marketplace Web Service (MWS) umbrella for years.
; IAM ARN. This is the identifier for the AWS IAM user we created in the last post (it should look something like arn:aws:iam::012345678987:user/sp-api…and no, that's not my real info ;)
; Again, go to Partner Network > Develop Apps, then open the dropdown next to the Edit button for your app and select Authorize. This will bring you to the authorization page, where you can generate a Login with Amazon (LWA) refresh token. This token lasts a long time, and will be used to generate new API access tokens (which expire quickly). Save the LWA refresh token somewhere—you're going to need it! Note that from the Develop Apps page, you can click View to see your LWA credentials…you'll need those too.

; aws.accessKeyId =
; aws.secretKey =
; aws.region = us-east-1
; lwa.authEndpoint = https://api.amazon.com/auth/o2/token
; lwa.clientId =
; lwa.clientSecret =
; lwa.refreshToken =
; spapi.endpoint = https://sellingpartnerapi-na.amazon.com
; spapi.marketplaceIds = ATVPDKIKX0DER 
; https://jesseevers.com/selling-partner-api-access/