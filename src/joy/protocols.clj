(ns protocols)

(defrecord TreeNode [val l r])

;; (TreeNode. 5 nil nil)


(defn xconj [t v]
  (cond
   (nil? t)         (TreeNode. v nil nil)
   (< v (:val t))   (TreeNode. (:val t) (xconj (:l t) v) (:r t))
   :else            (TreeNode. (:val t) (:l t) (xconj (:r t) v))))

(defn xseq [t]
  (when t
    (concat (xseq (:l t)) [(:val t)] (xseq (:r t)))))

(def sample-tree (reduce xconj nil [3 5 2 4 6]))
;;(xseq sample-tree)

(defprotocol FIXO
  (fixo-push [fixo value])
  (fixo-pop [fixo])
  (fixo-peek [fixo]))

(extend-type TreeNode
  FIXO
  (fixo-push [node value]
	     (xconj node value)))

;; (xseq (fixo-push sample-tree 5/2))

(extend-type clojure.lang.IPersistentVector
  FIXO
  (fixo-push [vector value]
	     (conj vector value)))

;; (fixo-push [2 3 4 5 6] 5/2)

(extend-type nil
  FIXO
  (fixo-push [t v]
	     (TreeNode. v nil nil)))

(xseq (reduce fixo-push nil [3 5 2 4 6 0]))

(extend-type TreeNode
  FIXO
  (fixo-push [node value]
	     (xconj node value))
  (fixo-peek [node]
	     (if (:l node)
	       (recur (:l node))
	       (:val node)))
  (fixo-pop [node]
	    (if (:l node)
	      (TreeNode. (:val node) (fixo-pop (:l node)) (:r node))
	      (:r node))))

(extend-type clojure.lang.IPersistentVector
  FIXO
  (fixo-push [vector value]
	     (conj vector value))
  (fixo-peek [vector]
	     (peek vector))
  (fixo-pop [vector]
	    (pop vector)))

(defn fixo-into [c1 c2]
  (reduce fixo-push c1 c2))

(xseq (fixo-into (TreeNode. 5 nil nil) [2 4 6 7]))
(seq (fixo-into [5] [2 3 6 7]))

(def tree-node-fixo
     {:fixo-push (fn [node value]
		   (xconj node value))
      :fixo-peek (fn [node]
		   (if (:l node)
		     (recur (:l node)) ;; this recursive call is not polymorfic
		     (:val node)))
      :fixo-pop (fn [node]
		  (if (:l node)
		    (TreeNode. (:val node) (fixo-pop (:l node)) (:r node))
		    (:r node)))})

(extend TreeNode FIXO tree-node-fixo)

(xseq (fixo-into (TreeNode. 5 nil nil) [2 4 6 7]))

(defn fixed-fixo
  ([limit] (fixed-fixo limit []))
  ([limit vecotr]
     (reify FIXO
	    (fixo-push [this value]
		       (if (< (count vector) limit)
			 (fixed-fixo limit (conj vector value))
			 this))
	    (fixo-peek [_] (peek vector))
	    (fixo-pop [_] (pop vector)))))

(defrecord TreeNode [val l r]
  FIXO
  (fixo-push [t v]
	     (if (< v val)
	       (TreeNode. val (fixo-push l v) r)
	       (TreeNode. val l (fixo-push r v))))
  (fixo-peek [node]
	     (if l
	       (fixo-peek l)  ;; this recursive call is polymorfic!
	       val))
  (fixo-pop [t]
	    (if l
	      (TreeNode. val (fixo-pop l) r)
	      r)))

(def sample-tree2 (reduce fixo-push (TreeNode. 3 nil nil) [5 2 4 6]))
(xseq sample-tree2)


;; 9.3.3
(comment
  ;; doomed
  (defrecord InfiniteConstant [i]
    clojure.lang.ISeq
    (seq [this]
	 (lazy-seq (cons i (seq this))))))

(deftype InfiniteConstant [i]
    clojure.lang.ISeq
    (seq [this]
	 (lazy-seq (cons i (seq this)))))

(take 3 (lazy-seq (InfiniteConstant. 5)))

(deftype TreeNode [val l r]
  FIXO
  (fixo-push [_ v]
	     (if (< v val)
	       (TreeNode. val (fixo-push l v) r)
	       (TreeNode. val l (fixo-push r v))))
  (fixo-peek [_]
	     (if l
	       (fixo-peek l)  ;; this recursive call is polymorfic!
	       val))
  (fixo-pop [_]
	    (if l
	      (TreeNode. val (fixo-pop l) r)
	      r))
  
  clojure.lang.IPersistentStack
  
  (cons [this v] (fixo-push this v))
  (peek [this] (fixo-peek this))
  (pop [this] (fixo-pop this))

  clojure.lang.Seqable
  (seq [t]
       (concat (seq l) [val] (seq r))))

(extend-type nil
  FIXO
  (fixo-push [t v]
	     (TreeNode. v nil nil)))
(def sample-tree2 (reduce fixo-push (TreeNode. 3 nil nil) [5 2 4 6]))
(seq sample-tree2)