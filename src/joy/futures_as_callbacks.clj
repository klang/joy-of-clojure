(ns joy.futures-as-callbacks
  (:require (clojure [xml :as xml])
	    (clojure [zip :as zip]))
  (:import (java.util.regex Pattern)))

(defmulti rss-children class)
(defmethod rss-children String [uri-str]
	   (-> (xml/parse uri-str)
	       zip/xml-zip
	       zip/down
	       zip/children))

(defn count-tweet-text-task [txt feed]
  (let [items (rss-children feed)
	re    (Pattern/compile (Pattern/quote txt))]
    (count
     (mapcat #(re-seq re (first %))
	     (for [item (filter (comp #{:item} :tag) items)]
	       (-> item :content first :content))))))

(comment
  (count-tweet-text-task
   "#clojure"
   "http://twitter.com/statuses/user_timeline/46130870.rss"))

(defmacro as-futures [[a args] & body]
  (let [parts          (partition-by #{'=>} body)
	[acts _ [res]] (partition-by #{:as} (first parts))
	[_ _ task]     parts]
    `(let [~res (for [~a ~args] (future ~@acts))]
       ~@task)))

(defn tweet-occurrences [tag & feeds]
  (as-futures [feed feeds]
	      (count-tweet-text-task tag feed)
	      :as results
	      =>
	      (reduce (fn [total res] (+ total @res))
		      0
		      results)))

#_(tweet-occurrences
 "#clojure"
 "http://twitter.com/statuses/user_timeline/46130870.rss"
 "http://twitter.com/statuses/user_timeline/14375110.rss"
 "http://twitter.com/statuses/user_timeline/5156041.rss"
 "http://twitter.com/statuses/user_timeline/21439272.rss")