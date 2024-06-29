(ns demo.repl.identity.permission
  (:require
   [modular.permission.user :refer [find-user-id-via-email get-user-roles]]
   [modular.system]))

(def p (modular.system/system :permission))

(get-user-roles p :john)
(get-user-roles p :boss)
(get-user-roles p :demo)
(get-user-roles p :florian)
(get-user-roles p :bongo) ; this user is unknown, and should have role nil

(find-user-id-via-email p "hoertlehner@gmail.com")
