(ns profile.simpledb
  (:refer-clojure :exclude [keys get get-in remove]))

(defonce ^:dynamic *db* (atom {}))

(defn put! [k v]
  (swap! *db* assoc k v)
  [k v])

(defn get [k]
  (clojure.core/get @*db* k))

(defn get-in [k ks]
  (clojure.core/get-in (get k) ks))

(defn keys [k]
  (clojure.core/keys (get k)))

(defn remove! [k]
  (swap! *db* dissoc k)
  k)

(defn update! [k f & args]
  (clojure.core/get
    (swap! *db* #(assoc % k (apply f (clojure.core/get % k) args)))
    k))

(defn clear! []
  (reset! *db* {}))

(defn init! [content]
  (reset! *db* content))