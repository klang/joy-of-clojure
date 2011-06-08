(ns joy.locks
  (:use [joy.mutation :only (dothreads!)])
  (:refer-clojure :exclude [aget aset count seq])
  (:require [clojure.core :as clj]))

(defprotocol SafeArray
  (aset  [this i f])
  (aget  [this i])
  (count [this])
  (seq   [this]))

(defn make-dumb-array [t sz]
  (let [a (make-array t sz)]
    (reify
     SafeArray
     (count [_] (clj/count a))
     (seq   [_] (clj/seq a))
     (aget  [_ i] (clj/aget a i))
     (aset  [this i f] (clj/aset a i (f (aget this i)))))))

(defn pummel [a]
  (dothreads! #(dotimes [i (count a)] (aset a i inc)) :threads 100))

(def D (make-dumb-array Integer/TYPE 15))

;; (pummel D)
;; joy.locks> (seq D)
;; (100 100 100 100 100 100 100 68)

;; the entire array is locked for a read or write, i.e. threads wanting
;; access are doing a lot of waiting, but things are done correctly.
(defn make-dumb-array [t sz]
  (let [a (make-array t sz)]
    (reify
     SafeArray
     (count [_] (clj/count a))
     (seq   [_] (clj/seq a))
     (aget  [_ i] (locking a (clj/aget a i)))
     (aset  [this i f] (locking a (clj/aset a i (f (aget this i))))))))

(def A (make-dumb-array Integer/TYPE 15))
;;(pummel A)
;;(seq A)

(defn lock-i [target-index num-locks]
  (mod target-index num-locks))

(import 'java.util.concurrent.locks.ReentrantLock)

(defn make-smart-array [t sz]
  (let [a   (make-array t sz)
	Lsz (quot sz 2)
	L   (into-array (take Lsz (repeatedly #(ReentrantLock.))))]
    
    (reify
     SafeArray
     (count [_] (clj/count a))
     (seq   [_] (clj/seq a))
     (aget  [_ i]
	    (let [lk (clj/aget L (lock-i (inc i) Lsz))]
	      (.lock lk)                   ;; <--- explicit locking
	      (try
		(clj/aget a i)
		(finally (.unlock lk)))))  ;; <--- explicit unlocking
     (aset  [this i f]
	    (let [lk (clj/aget L (lock-i (inc i) Lsz))]
	      (.lock lk)
	      (try
		(clj/aset a i (f (aget this i)))
		(finally (.unlock lk))))))));; <--- reentrant locking

(def S (make-smart-array Integer/TYPE 15))
;; (pummel S)
;; (seq S)