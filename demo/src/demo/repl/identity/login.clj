(ns repl.identity.login
  (:require
   [modular.system]
   [token.identity.local :refer [get-token verify-token]]))


(def this (modular.system/system :oauth2))

(get-token this "bongo" "11111")
(get-token this "demo" "11111")
(get-token this "demo" "1234")

(verify-token this "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VyIjoiZGVtbyJ9.mWO7pjUFhFpEDeQT_3OjM1YCZ1TN8LNZiA_3xF-NkBI")

(verify-token this "eyJhbGciOiJIUzI1NiJ9.eyJ1c2xyIjoiZGVtbyJ9.mWO7pjUFhFpEDeQT_3OjM1YCZ1TN8LNZiA_3xF-NkBI")
