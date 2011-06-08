(ns elevator)

(defn elevator [commands]
  (letfn
      [(ff-open [[cmd & r]]
		"When the elevator is open on the 1st floor, it can either close or be done."
		#(case cmd
		       :close (ff-closed r)
		       :done  true
		       false))
       (ff-closed [[cmd & r]]
		  "When the elevator is closed on the 1st floor, it can either open or go up."
		  #(case cmd
			 :open (ff-open r)
			 :up   (sf-closed r)
			 false))
       ;; top floor
       (sf-closed [[cmd & r]]
		  "When the elevator is closed on the 2nd floor, it can either go down or open"
		  #(case cmd
			 :down (ff-closed r)
			 :open (sf-open r)
			 false))
       (sf-open [[cmd & r]]
		"When the elevator is open on the 2nd floor, it can either close or be done"
		#(case cmd
		       :close (sf-closed r)
		       :done true
		       false))]
    (trampoline ff-open commands)))

(elevator [:close :open :done])
(elevator [:close :open :close :up :open :open :done])
(elevator [:close :up :open :close :down :open :done])
