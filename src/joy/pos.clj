(ns pos
  (:use clojure.test))


(defn pos1 [e coll]
  (let [cmp (if (map? coll)
	      #(= (second %1) %2)
	      #(= %1 %2))]
    (loop [s coll idx 0]
      (when (seq s)
	(if (cmp (first s) e)
	  (if (map? coll)
	    (first (first s))
	    idx)
	  (recur (next s) (inc idx)))))))

(deftest test-pos1
  (is (= 5 (pos1 3 [:a 1 :b 2 :c 3 :d 4])))
  (is (nil? (pos1 :foo [:a 1 :b 2 :c 3 :d 4])))
  (is (= :c (pos1 3 {:a 1 :b 2 :c 3 :d 4})))
  (is (= 5 (pos1 3 '(:a 1 :b 2 :c 3 :d 4))))
  (is (= 13 (pos1 \3 ":a 1 :b 2 :c 3 :d 4"))))

(defn index [coll]
  (cond (map? coll) (seq coll)
	(set? coll) (map vector coll coll)
	:else (map vector (iterate inc 0) coll)))

(defn pos2 [e coll]
  (for [[i v] (index coll) :when (= e v)] i))

(deftest test-pos2
  (is (= '(5) (pos2 3 [:a 1 :b 2 :c 3 :d 4])))
  (is (= '(:c) (pos2 3 {:a 1 :b 2 :c 3 :d 4})))
  (is (= '(1 3 5) (pos2 3 [:a 3 :b 3 :c 3 :d 4])))
  (is (= '(:a :b :c) (pos2 3 {:a 3 :b 3 :c 3 :d 4})))
  (is (= '(13) (pos2 \3 ":a 1 :b 2 :c 3 :d 4"))))

(defn pos [pred coll]
  (for [[i v] (index coll) :when (pred v)] i))

(deftest test-pos
  (is (= '(:c :d) (pos #{3 4} {:a 1 :b 2 :c 3 :d 4})))
  (is (= '(0 2) (pos even? [2 3 6 7]))))

(run-tests)

