(ns rec)

(defn gcd [x y]
  (cond
   (> x y) (gcd (- x y) y)
   (< x y) (gcd x (- y x))
   :else x))

(defn gcd [x y]
  (cond
   (> x y) (recur (- x y) y)
   (< x y) (recur x (- y x))
   :else x))