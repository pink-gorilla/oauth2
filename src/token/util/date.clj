(ns token.util.date
  (:require
   [tick.core :as t]))

; #time/date       java.time.LocalDate
; #time/date-time  java.time.LocalDateTime
; #time/instant    java.time.Instant (milliseconds)
; #inst            java.date

; now

(defn now-instant []
  (t/now))

(defn add-seconds [dt n]
  (t/>> dt (t/new-duration n :seconds)))

(defn add-minutes [dt n]
  (t/>> dt (t/new-duration n :minutes)))

(comment
  (now-instant)
  (-> (now-instant) (add-seconds 1800))

  (t/> (-> (now-instant) (add-minutes -1))
       (now-instant))

 ; 
  )