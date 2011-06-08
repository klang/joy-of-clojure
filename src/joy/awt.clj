(ns joy-of-clojure.AwtCloseButtonEvent
  (meta {:description "translation of simple java class to clojure"
	 :url ["http://www.roseindia.net/java/example/java/awt/AwtCloseButtonEvent.shtml"
	       "http://www.mail-archive.com/clojure@googlegroups.com/msg11851.html"]})
  (:import (java.awt Frame Label Dimension)
	   (java.awt.event WindowEvent WindowAdapter)))

(def frame
     (doto (Frame. "Close Operation Frame")
       (.add (Label. "Welcom in Roseindia.net Tutorial" (Label/CENTER)))
       (.setSize (Dimension. 400 400))
       (.setVisible true)
       (.addWindowListener
	(proxy [WindowAdapter] []
	  (windowClosing [e] (.dispose frame))))))
