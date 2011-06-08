(ns fr-klokken
  (:use [clojure.contrib.combinatorics :only (cartesian-product)]
	[clojure.set :only (union)]))


;; 00:00:00
;; hr:min:sec
;; There are 8640 combinations if the time is called at 10 second intervals during the day

;; To write these calls, the number of different words would be
;;   the numbers from 0 to 19
;; + the numbers from 20, 30, 40 and 50
;; + the binder between minutes and seconds
;; = 25 words all in all
;;------------------------------------------------------------------------------

(comment
  (def danish
       {:words {0 "nul" 1  "en" 2 "to" 3 "tre" 4 "fire" 5 "fem" 6 "seks" 7 "syv" 8 "otte"
		9  "ni" 10 "ti" 11 "elleve" 12 "tolv" 13 "tretten" 14 "fjorten"
		15 "femten" 16 "seksten" 17 "sytten" 18 "atten" 19 "nitten"
		20 "tyve" 30 "tredive" 40 "fyrre" 50 "halvtres"}
	:padding "og"
	:order reverse})

  (def british 
       {:words {1 "one" 2 "two" 3 "three" 4 "four" 5 "five" 6 "six" 7 "seven" 8 "eight" 
		9 "nine" 10 "ten" 11 "eleven" 12 "twelve" 13 "thirteen" 14 "fourteen" 
		15 "fifteen" 16 "sixteen" 17 "seventeen" 18 "eighteen" 19 "nineteen" 
		20 "twenty" 30 "thirty" 40 "forty" 50 "fifty"}
	:padding " "
	:order identity})

  (defn spoken [language number]
    (cond
     (< number 20) {:number ((:words language) number)}
     :else
     (zipmap [:tens :ones]
	     (map (:words language) [(* 10 (quot number 10)) (rem number 10)]))))

  (defn spoken
    "returns the number as a word in the language specified"
    [language number]
    (cond
     (< number 20) (let [parts (vector ((:words language) number))]
		     {:spoken (apply str parts) :parts parts :number number})
     :else         (let [words ((:order language)
				(map (:words language)
				     [(* 10 (quot number 10)) (rem number 10)]))
			 parts (vector (first words) (:padding language) (second words))]
		     {:spoken (apply str parts) :parts parts :number number})))

  (defn spoken
    "returns the number as a word in the language specified"
    [language number]
    (cond
     (< number 20) (let [parts (vector ((:words language) number))]
		     {:spoken (apply str parts) :parts parts :number number})
     :else         (let [words ((:order language)
				(map (:words language)
				     [(* 10 (quot number 10)) (rem number 10)]))
			 parts (vector (first words) (:padding language) (second words))]
		     {:spoken (apply str parts) :parts parts :number number}))))


(def danish
     {0 "nul" 1  "en" 2 "to" 3 "tre" 4 "fire" 5 "fem" 6 "seks" 7 "syv" 8 "otte"
      9  "ni" 10 "ti" 11 "elleve" 12 "tolv" 13 "tretten" 14 "fjorten"
      15 "femten" 16 "seksten" 17 "sytten" 18 "atten" 19 "nitten"
      20 "tyve" 30 "tredive" 40 "fyrre" 50 "halvtres" :binder "og"})

(def british 
     {1 "one" 2 "two" 3 "three" 4 "four" 5 "five" 6 "six" 7 "seven" 8 "eight" 
      9 "nine" 10 "ten" 11 "eleven" 12 "twelve" 13 "thirteen" 14 "fourteen" 
      15 "fifteen" 16 "sixteen" 17 "seventeen" 18 "eighteen" 19 "nineteen" 
      20 "twenty" 30 "thirty" 40 "forty" 50 "fifty" :binder " "})

;; ---------------------------------------------------------------- all that is needed
;; 25 samples in all
(defn write [number]
  (let [words danish 
	parts {:ones (rem number 10) :tens (* 10 (quot number 10))}]
    (if (or (zero? (parts :ones))
	    (< number 20))
      (words number)
      (apply str (vector (words (parts :ones)) (words :binder) (words (parts :tens))))
      #_(apply str (vector (words (parts :tens)) (words :binder) (words (parts :ones)))))))

;; in Danish, the ones are called out before the tens, which is why they have been flipped.
;; an English version of the same function would of course use the second form.

;; as a matter of style in the spoken language, the minutes are called out with a padded zero
;; if the minutes are below 10. In Danish, this is said the same way as the word for zero, in
;; English, it would be called out as "oh"
(defn call
  ([[hours minutes seconds]] (call hours minutes seconds))
  ([hours minutes seconds]
     (let [words {:binder "og"}]
       (vector (write hours)
	       (let [m (write minutes)]
		 (cond (zero? minutes) (str m m)
		       (< minutes 10) (str (write 0) m)
		       :else m))
	       (words :binder)
	       (write seconds)))))
;; -----------------------------------------------------------------------------------------------

;; a couple of attempts to get around making a map containing the 71 samples
(comment
  (def samples
       (union (zipmap (map  #(-> (str "0" %) str keyword) (range 0 10)) (map write (range 0 10)))
	      (zipmap (map  #(-> % str keyword) (range 0 60)) (map write (range 0 60)))
	      {:binder "og"}))

  (def samples
       (union (zipmap (map  #(-> % str keyword) (range 0 10)) (map write (range 0 10)))
	      (zipmap (range 0 60) (map write (range 0 60)))
	      {:binder "og"})))

;; =============================================================================
;; Putting together samples from the 25 words would probably not sound very natural
;; and longer samples would be needed:
;;   the numbers from 0 to 59 without zero padding (hour and second samples)
;; + the numbers from 0 to 9 with zero padding     (minute samples for oh-oh, oh-one, etc..)
;; + the binder between minutes and seconds
;; = 71 samples all in all

;; ..... a simple table with 71 samples, making the
(def write-zero-number
     {6 "nulseks", 7 "nulsyv", 4 "nulfire", 5 "nulfem", 1 "nulen",
      0 "nulnul", 3 "nultre", 2 "nulto", 9 "nulni", 8 "nulotte"})

(def write-number
     (zipmap (range 0 60) (map write (range 0 60))))

(defn call
  ([[hours minutes seconds]] (call hours minutes seconds))
  ([hours minutes seconds]
     (vector (write-number hours)
	     (cond (zero? minutes) (write-zero-number 0)
		   (< minutes 10) (write-zero-number minutes)
		   :else (write-number minutes))
	     "og"
	     (write-number seconds))))

;; in reality, 8640 samples were used .. one for every 10 second interval during a day!
(def sample-seq
     (for [hours (range 24)
	   minutes (range 60)
	   seconds (range 0 60 10)]
       (call [hours minutes seconds])))

;; a call has to be taken out of the sample-sequence and presented in the gap between two bells
;; (call 12 41 10) --beep-- (call 12 41 20) --beep--
