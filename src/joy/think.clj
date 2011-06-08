(ns joy.think
  (:use [joy.futures-as-callbacks
	 :only (rss-children tweet-occurrences count-tweet-text-task)]))

(defn with-redefs-fn [binding-map func & args]
  (let [root-bind (fn [m]
		    (doseq [[a-var a-val] m] (.bindRoot a-var a-val)))
	old-vals (zipmap (keys binding-map)
			 (map deref (keys binding-map)))]
    (try
      (root-bind binding-map)
      (apply func args)
      (finally
       root-bind old-vals))))

(defmacro with-redefs [bindings & body]
  `(with-redefs-fn ~(zipmap (map #(list `var %) (take-nth 2 bindings))
			    (take-nth 2 (next bindings)))
     (fn [] ~@body)))

(defn tweetless-rss-children [s]
  '({:tag :title :attrs nil :content ["Stub"]}))

(defn count-rss2-children [s]
  (count (rss-children s)))

#_(with-redefs [rss-children tweetless-rss-children]
    (count-rss2-children "dummy"))

#_(with-redefs [rss-children tweetless-rss-children]
  (tweet-occurrences "dummy" "test-url"))

(require '[clojure.test :as test])

(test/deftest feed-tests
  (with-redefs [rss-children tweetless-rss-children]
    (test/testing
     "RSS2 child counting"
     (test/is (= 1 (count-rss2-children "dummy"))))
    (test/testing
     "Twitter occurrence counting"
     (test/is (= 0 (count-tweet-text-task "#clojure" ""))))))
