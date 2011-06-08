(ns qsort)

(defn nom [n] (take n (repeatedly #(rand-int n))))

(defn sort-parts
  "Lazy, tail-recursive, incremental quicksort. Works against
   and creates partitions based on the pivot, defined as 'work'."
  [work]
  (lazy-seq
   (loop [[part & parts] work]            ;; pull apart work
     (if-let [[pivot & xs] (seq part)]
       (let [smaller? #(< % pivot)]       ;; define pivot comparison fn
	 (recur (list*
		 (filter smaller? xs)     ;; work all < pivot
		 pivot                    ;; work pivot itself
		 (remove smaller? xs)     ;; work all > pivot
		 parts)))                 ;; concat parts 
       (when-let [[x & parts] parts]
	 (cons x (sort-parts parts))))))) ;; sort rest if more parts

(defn qsort [xs]
  (sort-parts (list xs)))

(qsort [2 1 4 3])

(qsort (nom 20))

(first (qsort (nom 100)))

(take 10 (qsort (nom 10000)))