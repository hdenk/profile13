(ns profile.models-tests
  (:use [clojure.test :only [deftest is testing use-fixtures]])
  (:require [profile.simpledb :as simpledb]
            [profile.models :as models]))

(def dbcontent
  {:user 
   {1 {:id 1 :email "name1@noc.de" :name "name1" :comment "comment name1" :password "$2a$12$TGl2FP/k3hLnlhYH.198q.IuqibqE2EHo2pwTjgiys23K4bCVu.Ti"} ; test1
    2 {:id 2 :email "name2@noc.de" :name "name2" :comment "comment name2" :password "$2a$12$B.Lmz4uCROJRPSAuWugrm.3PErKfFN59FQtRQ6GBO4r2rbp6TbH/2"} ; test2
    3 {:id 3 :email "admin@noc.de" :name "admin" :comment "comment admin" :password "$2a$12$7qKyln6.X2Spz379RWAA5.Ce7N0/kbBZibtOfdwxyfR3n64ehmonO"}}}) ; secret

(defn initdb [test-function]
  (simpledb/init! dbcontent)
  (test-function))

(use-fixtures :each initdb)

(deftest user
  (testing "find-fns"
    (is 
      (= 
        "name2@noc.de"
        (:email (models/find-user-by-id 2))))
    (is 
      (= 
        nil
        (:email (models/find-user-by-id :unknown)))))
  (testing "login"
    (is 
      (= 
        "name2@noc.de"
        (:email (models/login {:id 2 :password "test2"}))))
    (is 
      (= 
        nil
        (models/login {:id 2 :password "fail"})))))    
