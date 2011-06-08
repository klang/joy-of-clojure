(ns joy-of-clojure.scratch

  ;; for mandelbrot
(:refer-clojure :exclude [+ * <])
  (:use (clojure.contrib complex-numbers)
        (clojure.contrib.generic [arithmetic :only [+ *]]
                                 [comparison :only [<]]
                                 [math-functions :only [abs]])
	[clojure.contrib.seq-utils :only (indexed)])
  )

(def x 42)
(.start (Thread. #(println "Answer: " x)))
; Answer: 42

(defn print-seq [s]
  (when (seq s)
    (prn (first s))
    (recur (rest s))))

(defn xors
  [max-x max-y]
  (for [x (range max-x) y (range max-y)] [x y (bit-xor x y) 256]))

(comment
  (for [method (seq (.getMethods java.awt.Frame))
        :let [method-name (.getName method)]
        :when (re-find #"Vis" method-name)]
    method-name))

(def frame (java.awt.Frame.))
(.setVisible frame true)
(.setSize frame (java.awt.Dimension. 200 200))

(def gfx (.getGraphics frame))
(.fillRect gfx 100 100 50 75)
(.setColor gfx (java.awt.Color. 255 128 0))
(.fillRect gfx 100 150 75 50)

(comment
  (doseq [[x y xor] (xors 200 200)]
    (.setColor gfx (java.awt.Color. xor xor xor))
    (.fillRect gfx x y 1 1)))

(defn xors
  [max-x max-y]
  (for [x (range max-x) y (range max-y)] [x y (rem (bit-xor x y) 256)]))


(.setSize frame (java.awt.Dimension. 500 500))

(comment
  (doseq [[x y xor] (xors 500 500)]
    (.setColor gfx (java.awt.Color. xor xor xor))
    (.fillRect gfx x y 1 1)))

(defn clear [g] (.clearRect g 0 0 500 500))

(defn f-values [f xs ys]
  (for [x (range xs) y (range ys)]
    [x y (rem (f x y) 256)]))

(defn xors
  [max-x max-y]
  (f-values bit-xor max-x max-y))

(defn draw-values [f xs ys]
  (clear gfx)
  (.setSize frame (java.awt.Dimension. xs ys))
  (doseq [[x y v] (f-values f xs ys)]
    (.setColor gfx (java.awt.Color. v v v))
    (.fillRect gfx x y 1 1)))

;;----
(defn mandelbrot? [z] 
  (loop [c 1
	 m (iterate #(+ z (* % %)) z)]
    (if (and (> 20 c) 
	     (< (abs (first m)) 2) )
      (recur (inc c) 
	     (rest m))
      (if (= 20 c) true false))))

(defn mandelbrot []
  (for [y (range 1 -1 -0.05)      ;; 40 x 80 points
	x (range -2 0.5 0.0315)] 
    (if (mandelbrot? (complex x y)) "#" " ")))

(comment
  (println (interpose \newline (map #(apply str %) (partition 80 (mandelbrot))))))
;;----

(defn plot [[x y] v]
  (.setColor gfx (java.awt.Color. v v v))
  (.fillRect gfx x y 1 1))

(defn step [start end width] (float (/ (- end start) width )))
(defn stepped-range [start end width] (range start end (step start end width)))

(defn mandelbrot? [z] 
  (loop [c 1
	 m (iterate #(+ z (* % %)) z)]
    (if (and (> 20 c) 
	     (< (abs (first m)) 2) )
      (recur (inc c) 
	     (rest m))
      (if (= 20 c) true false))))

(defn transform [x y]
  (let [menu-bar-height 23
	border-width     3]
    [(+ x border-width)
     (+ y menu-bar-height)]))

(defn mandelbrot [width height]
  (for [y (indexed (stepped-range  1 -1   width))
	x (indexed (stepped-range -2  0.5 height))]
    (plot (transform (first x) (first y))
	  (if (mandelbrot? (complex (second x) (second y))) 0 255))))

(comment
  (clear gfx)
  (mandelbrot 200 250))

(defn mandelbrot [width height]
  (for [y (indexed (stepped-range  1 -1   height))
	x (indexed (stepped-range -2  0.5 width))]
    [(first x) (first y) (if (mandelbrot? (complex (second x) (second y))) 0 255)]))

(defn draw-mandelbrot [xs ys]
  (clear gfx)
  (.setSize frame (java.awt.Dimension. xs ys))
  (doseq [[x y v] (mandelbrot xs ys)]
    (.setColor gfx (java.awt.Color. v v v))
    (.fillRect gfx x y 1 1)))

;;; -- with colors

(defn mandelbrot? [z] 
  (loop [c 1
	 m (iterate #(+ z (* % %)) z)]
    (if (and (> 64 c) 
	     (< (abs (first m)) 2) )
      (recur (inc c) 
	     (rest m))
      (if (= 64 c) 0 c))))

(defn mandelbrot [width height]
  (for [y (indexed (stepped-range  1 -1   height))
	x (indexed (stepped-range -2  0.5 width))]
    [(first x) (first y) (rem (mandelbrot? (complex (second x) (second y))) 256)]))

(def colors
     (assoc
	 (into {}
	       (for [n (range 0 64) :let [v (* n 4)]]
		 {(+   0 n) [(rem (+ v 128) 256) (* n 4) 0]
		  (+  64 n) [64 255 v]
		  (+ 128 n) [64 (- 255 v) 255]
		  (+ 192 n) [64 0 (- 255 v)]}))
       0 [0 0 0]))

(defn draw-mandelbrot [xs ys]
  (clear gfx)
  (.setSize frame (java.awt.Dimension. xs ys))
  (doseq [[x y v] (mandelbrot xs ys)
	  :let [[r g b] (colors v)]]
    (.setColor gfx (java.awt.Color. r g b))
    (.fillRect gfx x y 1 1)))

(defn mandelbrot-seq [x y]
  (let [z (complex x y)]
    (iterate #(+ z (* % %)) z)))
