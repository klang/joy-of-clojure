(ns joy.mutation
  (:import (java.util.concurrent Executors))
  (:use joy.neighbors))

(def *pool* (Executors/newFixedThreadPool
	     (+ 2 (.availableProcessors (Runtime/getRuntime)))))

(defn dothreads! [f & {thread-count :threads
		       exec-count :times
		       :or {thread-count 1 exec-count 1}}]
  (dotimes [t thread-count]
    (.submit *pool* #(dotimes [_ exec-count] (f)))))

(def to-move (ref [[:K [2 1]] [:k [0 1]]]))

(def initial-board
     [[:- :k :-]
      [:- :- :-]
      [:- :K :-]])

(defn board-map [f bd]
  (vec (map #(vec (for [s %] (f s))) bd)))

(defn reset!
  "Resets the board state. Generally these types of functions are a bad idea,
   but matters of page count fource our hand"
  []
  (def board (board-map ref initial-board))
  (def to-move (ref [[:K [2 1]] [:k [0 1]]]))
  (def num-moves (ref 0)))

(def king-moves (partial
		 neighbors
		 [[-1 -1] [-1 0] [-1 1] [0 -1]
		  [0 1] [1 -1] [1 0] [1 1]]3))

(defn good-move? [to enemy-sq]
  (when (not= to enemy-sq) to))

(defn choose-move [[[mover mpos] [_ enemy-pos]]]
  [mover (some #(good-move? % enemy-pos)
	       (shuffle (king-moves mpos)))])

(comment
  (reset!)
  (take 5 (repeatedly #(choose-move @to-move))))

(defn place [from to] to)

(defn move-piece [[piece dest] [[_ src] _]]
  (alter (get-in board dest) place piece)
  (alter (get-in board src) place :-)
  (alter num-moves inc))

(defn update-to-move [move]
  (alter to-move #(vector (second %) move)))

(defn make-move []
  (dosync
   (let [move (choose-move @to-move)]
     (move-piece move @to-move)
     (update-to-move move))))

(comment
  (make-move)
  (board-map deref board)
  @num-moves)

(defn go [move-fn threads times]
  (dothreads! move-fn :threads threads :times times))

(comment
  (go make-move 100 100)
  (board-map #(dosync (deref %)) board)
  @to-move
  @num-moves)

(defn bad-make-move []
  (let [move (choose-move @to-move)]
    (dosync (move-piece move @to-move))
    (dosync (update-to-move move))))

(comment
  (go bad-make-move 100 100)
  (board-map #(dosync (deref %)) board))

(defn move-piece [[piece dest] [[_ src] _]]
  (commute (get-in board dest) place piece)
  (commute (get-in board src) place :-)
  (commute num-moves inc))

(comment
  (go make-move 100 100)
  (board-map #(dosync (deref %)) board)
  @to-move
  @num-moves
  )

(defn update-to-move [move]
  (commute to-move #(vector (second %) move)))

(comment
  (go make-move 100 100)
  (board-map #(dosync (deref %)) board)
  @to-move
  @num-moves
  )

(dosync (ref-set to-move '[[:K [2 1]] [[:k [0 1]]]]))

;; refs under stress

(defn stress-ref [r]
  (let [slow-tries (atom 0)]
    (future
     (dosync
      (swap! slow-tries inc)
      (Thread/sleep 200)
      @r)
     (println (format "r is: %s, history: %d, after: %d tries"
		      @r (ref-history-count r) @slow-tries)))
    (dotimes [i 500]
      (Thread/sleep 10)
      (dosync (alter r inc)))
    :done))

(comment
  (stress-ref (ref 0))
  (stress-ref (ref 0 :max-history 30))
  (stress-ref (ref 0 :min-history 15 :max-history 30)))

;; controlling I/O with an Agent

(def log-agent (agent 0))
(defn do-log [msg-id message]
  (println msg-id ":" message)
  (inc msg-id))

(defn do-step [channel message]
  (Thread/sleep 1)
  (send-off log-agent do-log (str channel message)))

(defn three-step [channel]
  (do-step channel " ready to begin (step 0)")
  (do-step channel " warming up (step 1)")
  (do-step channel " really getting going now (step 2)")
  (do-step channel " done! (step 3)"))

(defn all-together-now []
  (dothreads! #(three-step "alpha"))
  (dothreads! #(three-step "beta"))
  (dothreads! #(three-step "omega")))

(comment
  (all-together-now)

  (send log-agent (fn [_] 100))
  (do-step "epsilon " "near miss"))

(defn exercise-agents [send-fn]
  (let [agents (map #(agent %) (range 10))]
    (doseq [a agents]
      (send-fn a (fn [_] (Thread/sleep 1000))))
    (doseq [a agents]
      (await a))))

(comment
  (time (exercise-agents send-off))
  (time (exercise-agents send)))

;; when to use Atoms

(def *time* (atom 0))
(defn tick [] (swap! *time* inc))
(comment
  (dothreads! tick :threads 1000 :times 100)
  @*time*)

(def *time* (java.util.concurrent.atomic.AtomicInteger. 0))
(defn tick [] (.getAndIncrement *time*))

(comment
  (dothreads! tick :threads 1000 :times 100)
  *time*)


;; memoization

(defn new-core-memoize [function]
  (let [cache (atom {})]
    (fn [& args]
      (or (second (find @cache args))
        (let [ret (apply function args)]
          (swap! cache assoc args ret)
          ret)))))

(defn manipulatable-memoize [function]
  (let [cache (atom {})]
    (with-meta
      (fn [& args]
	(or (second (find @cache args))
	    (let [ret (apply function args)]
	      (swap! cache assoc args ret)
	      ret)))
      {:cache cache})))

(def slowly (fn [x] (Thread/sleep 3000) x))

(comment
  (time [(slowly 9) (slowly 9)]))

(def sometimes-slowly (manipulatable-memoize slowly))
(comment
  (time [(sometimes-slowly 108) (sometimes-slowly 108)])
  (meta sometimes-slowly)
  (let [cache (:cache (meta sometimes-slowly))]
    (swap! cache dissoc '(108)))
  )


;;; chapter 12 memoization revisited

(defprotocol CacheProtocol
  (lookup  [cache e])
  (has?    [cache e])
  (hit     [cache e])
  (miss    [cache e ret]))

(deftype BasicCache [cache]
  CacheProtocol
  (lookup [_ item] (get cache item))
  (has?   [_ item] (contains? cache item))
  (hit    [this item] this)
  (miss   [_ item result] (BasicCache. (assoc cache item result))))

(def cache (BasicCache. {}))

;; (lookup (miss cache '(servo) :robot) '(servo))

(defn through [cache f item]
  (if (has? cache item)
    (hit cache item)
    (miss cache item (delay (apply f item)))))

(deftype PluggableMemoization [f cache]
  CacheProtocol
  (has?   [_ item] (has? cache item))
  (hit    [this item] this)
  (miss   [_ item result] (PluggableMemoization. f (miss cache item result)))
  (lookup [_ item] (lookup cache item)))

(defn memoization-impl [cache-impl]
  (let [cache (atom cache-impl)]
    (with-meta
      (fn [& args]
	(let [cs (swap! cache through (.f cache-impl) args)]
	  @(lookup cs args))
	#_(or (second (find @cache args))
	    (let [ret (apply function args)]
	      (swap! cache assoc args ret)
	      ret)))
      {:cache cache})))

(def slowly (fn [x] (Thread/sleep 3000) x))

(comment
  (time [(slowly 9) (slowly 9)]))

(def sometimes-slowly
     (memoization-impl
      (PluggableMemoization.
       slowly
       (BasicCache. {}))))

(comment
  (time [(sometimes-slowly 108) (sometimes-slowly 108)])
  (meta sometimes-slowly)
  )

