(ns joy.onlineres)

;; 1. Multiple Each Item in a List by 2
(map #(* 2 %) (range 1 11))

;; 2. Sum a List of Numbers
(reduce + (range 1 1001))

;; 3. Verify if Exists in a String
(def words #{"scala" "akka" "play framework" "sbt" "typesafe"})
(def tweet "This is an example tweet talking about scala and sbt.")
(filter #(contains? words %) (re-seq #"\S+" tweet))

;; 4. Read in a File
(def fileText (slurp "data.txt"))
(def fileLines (re-seq #"\S+" (slurp "data.txt")))

;; 5. Happy Birthday to You!
(def NAME "Karsten")
(println (apply str (map #(str "Happy Birthday " (if (= 3 %) (str "dear " NAME) (str "to You")) "\n") (range 1 5))))

;; 6. Filter list of numbers
(partition-by #(< 60 %) [49, 58, 76, 82, 88, 90])

;; 7. Fetch and Parse an XML web service
(require '(clojure [xml :as xml]))
(xml/parse "http://search.twitter.com/search.atom?&q=clojure")

;; 8. Find minimum (or maximum) in a List
(reduce min [14, 35, -7, 46, 98])
(reduce max [14, 35, -7, 46, 98])

;; 9. Parallel Processing
(pmap processItem lines)

;; 10. Sieve of Eratosthenes
;; (Bird & Wadler, Introduction to Functional Programming, p.175)
(defn sieve [xs]
  (filter #(not (zero? (mod % (first xs)))) (rest xs)))

(defn rsieve [xs]
  (map first (iterate sieve xs)))

(defn primes-below [max]
  (take-while #(not (nil? %)) 
	      (rsieve (range 2 max))))


