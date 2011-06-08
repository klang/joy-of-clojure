(ns joy.promises
  (:use [joy.mutation :only (dothreads!)]))

(def x (promise))
(def y (promise))
(def z (promise))

(dothreads! #(deliver z (+ @x @y)))
(dothreads! #(do (Thread/sleep 2000) (deliver x 52)))
(dothreads! #(do (Thread/sleep 4000) (deliver y 86)))
(time @z)