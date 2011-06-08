(ns euler
  (:use [clojure.contrib.math :only (expt)]))

(defn palindrome? [m]
  (= (apply str (reverse (str m))) (str m)))

(defn reverse-number [number]
  (loop [n number dl 0]
    (if (zero? n) dl
      (recur (quot n 10) (+ (* 10 dl) (rem n 10))))))

(defn palindrome? [n] (= (reverse-number n) n))

(defn solve004 [n]
  (reduce max (for [x (range (expt 10 (- n 1)) (expt 10 n)) 
		    y (range (expt 10 (- n 1)) (+ x 1))]
		(if (palindrome? (* x y)) (* x y) 0))))


;;..try counting down

(count
 (take 100 (for [x (range (expt 10 (- 3 1)) (expt 10 3)) 
		y (range (expt 10 (- 3 1)) (+ x 1))]
	    [x y])))