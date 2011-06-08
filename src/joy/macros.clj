(ns macros)

(-> 25 Math/sqrt int list)

(-> (/ 144 12) (/ ,,, 2 3) str keyword list)

(-> (/ 144 12) (* ,,, 4 (/ 2 3)) str keyword (list ,,, :33))
;;  '--------'-----^                                ^
;;  '--------------------------------------'--------o 

(->> a (+ 5 ,,,) (let [a 5] ,,,))
;;  '-'------^---------------^

(defn contextual-eval [ctx expr]
  (eval
   `(let [~@(mapcat (fn [[k v]] [k `'~v]) ctx)]
      ~expr)))

(contextual-eval {'a 1 'b 2} '(+ a b))
(contextual-eval {'a 1 'b 2} '(let [b 1000] (+ a b)))

(let [x 9 y '(- x)]
  (println `y)
  (println ``y)
  (println ``~y)
  (println ``~~y)
  (contextual-eval {'x 36} ``~~y))


(defmacro do-until [& clauses]
  (when clauses
    (list `when (first clauses)
	  (if (next clauses)
	    (second clauses)
	    (throw (IllegalArgumentException.
		    "do-until requires an even number of forms")))
	  (cons 'do-until (nnext clauses)))))

(do-until
 (even? 2) (println "Even")
 (odd?  3) (println "Odd")
 (zero? 1) (println "You will never see me")
 :lollipop (println "Truthy thing"))

;; macros> (macroexpand-1 '(do-until true (prn 1) false (prn 2)))
;; (clojure.core/when true (prn 1) (do-until false (prn 2)))

(require '[clojure.walk :as walk])
(walk/macroexpand-all '(do-until true (prn 1) false (prn 2)))

(defmacro unless [condition & body]
  `(if (not ~condition) (do ~@body)))

(unless (even? 3) "now we see it..")
(unless (even? 2) "now we don't.")

(defn from-end [s n]
  (let [delta (dec (- (count s) n))]
    (unless (neg? delta)
	    (nth s delta))))

(from-end (range 1 101) 10)

(defmacro def-watched [name & value]
  `(do
     (def ~name ~@value)
     (add-watch (var ~name)
		:re-bind
		(fn [~'key ~'r old# new#]
		  (println old# " -> " new#)))))
(def-watched x (* 12 12))
(def x 0)

(defmacro domain [name & body]
  `{:tag :domain
    :attrs {:name (str '~name)}
    :content [~@body]})

(declare handle-things)

(defmacro grouping [name & body]
  `{:tag :grouping
    :attrs {:name (str '~name)}
    :content [~@(handle-things body)]})

(declare grok-attrs grok-props)

(defn handle-things [things]
  (for [t things]
    {:tag :thing
     :attrs (grok-attrs (take-while (comp not vector?) t))
     :content (if-let [c (grok-props (drop-while (comp not vector?) t))]
		[c]
		[])}))

(defn grok-attrs [attrs]
  (into {:name (str (first attrs))}
	(for [a (rest attrs)]
	  (cond
	   (list? a) [:isa (str (second a))]
	   (string? a) [:comment a]))))

(defn grok-props [props]
  (when props
    {:tag :properties :attrs nil
     :content (apply vector (for [p props]
			      {:tag :property
			       :attrs {:name (str (first p))}
			       :content nil}))}))

(def d
     (domain man-vs-monster
	     (grouping people
		       (Human "A stock human")
		       (Man (isa Human)
			    "A man, baby"
			    [name]
			    [has-beard?]))
	     (grouping monsters
		       (Chupacabra
			"A fierce, yet elusive creature"
			[eats-goats?]))))

;; (use '[clojure.xml :as xml])
;; (xml/emit d)