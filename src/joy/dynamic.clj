(ns joy.dynamic)

(with-precision 4 (/ 1M 3))
(/ 1M 3)

;; map is lazy and the divisions are not done inside
;; with-precision's dynamic scope
(with-precision 4 (map (fn [x] (/ x 3)) (range 1M 4M)))

;; map is still lazy, but the dynamic scope of *math-context*
;; is recreated before each calculation.
(with-precision 4 (doall (map (fn [x] (/ x 3)) (range 1M 4M))))