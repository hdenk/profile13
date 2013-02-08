(ns profile.core-tests 
  (:use [clojure.test :only [deftest is testing use-fixtures]])
  (:require [profile.models :as models]
            [profile.views :as views]
            [profile.server :as server]))

; Allein das Laden der produktiven Namespaces
; ist schon ein Test
(deftest test-that-succeeds []
    (is (= 1 1)))  
