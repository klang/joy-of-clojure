(ns joy.neighbors)

(def matrix [[1 2 3]
	     [4 5 6]
	     [7 8 9]])

(get-in matrix [1 2])
(assoc-in matrix [1 2] 'x)
(update-in matrix [1 2] * 100)

(defn neighbors
  ([size yx] (neighbors [[-1 0] [1 0] [0 -1] [0 1]] size yx))
  ([deltas size yx]
     (filter (fn [new-yx]
	       (every? #(< -1 % size) new-yx))
	     (map #(map + yx %) deltas))))

(map #(get-in matrix %) (neighbors 3 [0 0]))