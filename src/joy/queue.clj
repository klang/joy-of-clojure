(ns queue)

(defmethod print-method clojure.lang.PersistentQueue
  [q w]
  (print-method '<- w) (print-method (seq q) w) (print-method '<- w))

(def schedule
     (conj clojure.lang.PersistentQueue/EMPTY
	   :wake-up :shower :brush-teeth))

(peek schedule)
(class (pop schedule))  ;; result is a queue 
(class (rest schedule)) ;; result is a sequence -> pop peek conj woun't behave as expected

