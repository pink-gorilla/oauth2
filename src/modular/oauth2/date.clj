(ns modular.oauth2.date
  (:require
   [tick.core :as t]
   [cljc.java-time.instant :as ti]
   [cljc.java-time.local-date :as ld]
   [cljc.java-time.local-date-time :as ldt]
   [cljc.java-time.zoned-date-time :as zdt]
   [cljc.java-time.zone-offset :refer [utc]]
   [cljc.java-time.format.date-time-formatter :as fmt :refer [of-pattern
                                                              ;iso-date
                                                              ]]))
; #time/date       java.time.LocalDate
; #time/date-time  java.time.LocalDateTime
; #time/instant    java.time.Instant (milliseconds)
; #inst            java.date

; now

(defn now-instant []
 (t/now))

(defn add-seconds [dt n]
  (t/>> dt (t/new-duration n :seconds)))

;; epoch conversion

(defn datetime->epoch-second [dt]
  (ldt/to-epoch-second dt utc))

(defn epoch-second->datetime [es]
  (-> es (ldt/of-epoch-second 1 utc)))

(defn add-days [dt n]
  (t/>> dt (t/new-duration n :days)))

(comment
  (now-instant)
  (-> (now-instant) (add-seconds 1800))

  (-> (t/now) (add-days 10))

  (epoch-second->datetime 1650389880)

  (t/inst "2022-04-11T13:15:59-04:00")
  (t/inst "2022-04-06T11:45:23")


(println "future: " (add-days (now-instant) 2) "now: " (now-instant))
(t/> (add-days (now-instant) 2) (now-instant))
 ; 
) 