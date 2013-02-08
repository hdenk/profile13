(ns profile.simpledb-tests
  (:use [clojure.test :only [deftest is testing use-fixtures]])
  (:require [profile.simpledb :as simpledb]))

(def dbcontent 
  {:user 
   {1 {:email "name1@noc.de" :name "name1" :comment "about name1"}
    2 {:email "name2@noc.de" :name "name2" :comment "about name2"}
    3 { :email "name3@noc.de" :name "name3" :comment "about name3"}}
   :post
   {1 {:subject "subject1" :text "text1"}
    2 {:subject "subject2" :text "text2"}}})

(defn initdb [test-function]
  (simpledb/init! dbcontent)
  (test-function))

(use-fixtures :each initdb)

(deftest init
  (testing "init"
    (is 
      (= 
        2
        (count @simpledb/*db*)))
    (is 
      (= 
        5
        (reduce #(+ %1 (count (second %2))) 0 @simpledb/*db*)))))

(deftest read
  (testing "keys"
    (is 
      (= 
        '(1 2 3)
        (simpledb/keys :user))))
  (testing "get"
    (is 
      (= 
        "name1@noc.de"
        (get-in (simpledb/get :user) [1 :email])))
    (is 
      (= 
        "subject2" 
        (get-in (simpledb/get :post) [2 :subject])))
    (is 
      (= 
        nil
        (get-in (simpledb/get :unknown) [:unknown :unknown]))))
  (testing "get-in"
    (is 
      (= 
        "name1"
        (simpledb/get-in :user [1 :name])))
    (is 
      (= 
        nil
        (simpledb/get-in :unknown [:unknown :unknown])))))

(deftest put!
  (testing "put!"
    (is 
      (= 
        "name4@noc.de"
        (do
          (simpledb/put! :user  {4 {:email "name4@noc.de" :name "name4" :comment "about name4"}})
          (simpledb/get-in :user [4 :email]))))))

(deftest remove1
  (testing "remove!"
    (is 
      (= 
        nil
        (do
          (simpledb/update! :user dissoc 2)
          (simpledb/get-in :user [2]))))))

(deftest remove2
  (testing "remove! unknown key"
    (is 
      (= 
        3
        (do
          (simpledb/update! :user dissoc :unknown)
          (count (simpledb/get :user)))))))

(deftest update
  (testing "update!"
    (is 
      (= 
        "changed"
        (do
          (simpledb/update! :user update-in [2 :name] (constantly "changed"))
          (simpledb/get-in :user [2 :name]))))))

(deftest clear
  (testing "update!"
    (is 
      (= 
        0
        (do (simpledb/clear!)
            (count @simpledb/*db*))))))
