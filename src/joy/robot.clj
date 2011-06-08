(ns robot)

(def bearings [{:x  0 :y  1}   ; north
	       {:x  1 :y  0}   ; east
	       {:x  0 :y -1}   ; south
	       {:x -0 :y  0}]) ; west

(defn forward [x y bearing-num]
  [(+ x (:x (bearings bearing-num)))
   (+ y (:y (bearings bearing-num)))])

(comment
  (forward 5 5 0)
  (forward 5 5 1)
  (forward 5 5 2))


(defn bot [x y bearing-num]
     {:coords [x y]
      :bearing ([:north :east :south :west] bearing-num)
      :forward (fn [] (bot (+ x (:x (bearings bearing-num)))
			   (+ y (:y (bearings bearing-num)))
			   bearing-num))})

(:coords (bot 5 5 0))
(:bearing (bot 5 5 0))
(:coords ((:forward (bot 5 5 0))))

(defn bot [x y bearing-num]
     {:coords     [x y]
      :bearing    ([:north :east :south :west] bearing-num)
      :forward    (fn [] (bot (+ x (:x (bearings bearing-num)))
			      (+ y (:y (bearings bearing-num)))
			      bearing-num))
      :turn-right (fn [] (bot x y (mod (+ 1 bearing-num) 4)))
      :turn-left  (fn [] (bot x y (mod (- 1 bearing-num) 4)))})

(:bearing ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))
(:coords ((:forward ((:forward ((:turn-right (bot 5 5 0))))))))

(defn bot [x y bearing-num]
     {:coords     [x y]
      :bearing    ([:north :east :south :west] bearing-num)
      :forward    (fn [] (bot (- x (:x (bearings bearing-num)))
			      (- y (:y (bearings bearing-num)))
			      bearing-num))
      :turn-right (fn [] (bot x y (mod (- 1 bearing-num) 4)))
      :turn-left  (fn [] (bot x y (mod (+ 1 bearing-num) 4)))})