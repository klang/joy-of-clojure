(ns joy.error-handling
  (:use [clojure.xml :as xml]))

(defn traverse [node f]
  (when node
    (f node)
    (doseq [child (:content node)]
      (traverse child f))))

(comment
  (traverse
   {:tag :flower :attrs {:name "Tanpopo"} :content []}
   println))

(def DB
     (-> "<zoo><pongo><animal>orangutan</animal></pongo><panthera><animal>Spot</animal><animal>lion</animal><animal>Lopshire</animal></panthera></zoo>"
	 .getBytes
	 (java.io.ByteArrayInputStream.)
	 xml/parse))


(defn ^{:dynamic true} handle-wierd-animal
  [{[name] :content}]
  (throw (Exception. (str name " must be 'dealt with'"))))

(defmulti visit :tag)
(defmethod visit :animal [{[name] :content :as animal}]
	   (case name
		 "Spot"     (handle-wierd-animal animal)
		 "Lopshire" (handle-wierd-animal animal)
		 (println name)))
(defmethod visit :default [node] nil)

(defmulti handle-wierd (fn [{[name] :content}] name))
(defmethod handle-wierd "Spot" [_]
	   (println "Transporting Spot to the circus."))
(defmethod handle-wierd "Lopshire" [_]
	   (println "Signing Lopshire to a book deal."))

(binding [handle-wierd-animal handle-wierd]
  (traverse DB visit))

(def _ (future
	(binding [handle-wierd-animal #(println (:content %))]
	  (traverse DB visit))))