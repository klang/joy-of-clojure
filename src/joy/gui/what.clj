(ns what)

(defmulti what-is class)
(defmethod what-is (Class/forName "[Ljava.lang.String;") [a] "1d String")
(defmethod what-is (Class/forName "[[Ljava.lang.Object;") [a] "2d Object")
(defmethod what-is (Class/forName "[[[[I") [a] "Primitive 4d int")

(doto (Thread. #(do (Thread/sleep 5000)
		    (println "haikeeba!")))
  .start)