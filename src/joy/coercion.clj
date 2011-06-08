(ns joy.coercion)

(defn occur-count [words]
  (let [res (atom {})]
    (doseq [w words] (swap! res assoc w (+ 1 (@res w 0))))
    @res))

(defn roll [n d]
  (reduce + (take n (repeatedly #(inc (rand-int d))))))

;; (time (dorun (occur-count (take 1000000 (repeatedly #(roll 3 6))))))
;; "Elapsed time: 8598.277115 msecs"

(defn occur-count [words]
  (let [res (atom {})]
    (doseq [w words]
      (let [v (int (@res w 0))]
	(swap! res assoc w (+ 1 v))))
    @res))

;; (time (dorun (occur-count (take 1000000 (repeatedly #(roll 3 6))))))
;; "Elapsed time: 8480.827247 msecs"


;; rethinking

(defn occur-count [words]
  (reduce #(assoc %1 %2 (inc (%1 %2 0))) {} words))

(defn roll [n d]
  (loop [n (int n), sum 0]
    (if (zero? n)
      sum
      (recur (dec n) (+ sum (inc (rand-int d)))))))
;; (time (dorun (occur-count (take 1000000 (repeatedly #(roll 3 6))))))
;; "Elapsed time: 4131.964362 msecs"

;; not a five fold improvement, like in the book, but ok.

;; (time (dorun (frequencies (take 1000000 (repeatedly #(roll 3 6))))))
;; "Elapsed time: 3332.0935 msecs"

;; pretty much as good as the build in function 'frequencies' 