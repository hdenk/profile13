(ns profile.models-tests
  (:use [clojure.test :only [deftest is testing use-fixtures]])
  (:require [profile.simpledb :as simpledb]
            [profile.models :as models]))

(def dbcontent 
  {:user 
   {1 {:id 1 :email "name1@noc.de" :password "test1" :name "name1" :comment "comment1"}
    2 {:id 2 :email "name2@noc.de" :password "test2" :name "name2" :comment "comment2"}
    3 {:id 3 :email "name3@noc.de" :password "test3" :name "name3" :comment "comment3"}}})

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
