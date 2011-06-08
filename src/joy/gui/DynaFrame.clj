(ns joy.gui.DynaFrame
  (:gen-class
   :name         joy.gui.DynaFrame
   :extends      javax.swing.JFrame
   :implements   [clojure.lang.IMeta]
   :prefix       df-
   :state        state
   :init         init
   :constructors {[String] [String]}
   :methods      [[display [java.awt.Container] void]
		  ^{:static true} [version [] String]])
  (:import (javax.swing JFrame JPanel)
	   (java.awt BorderLayout Container)))

(defn df-init [title] [[title] (atom {::title title})])
(defn df-meta [this] @(.state this))
(defn version [] "1.0")

(meta (joy.gui.DynaFrame. "3rd"))
(joy.gui.DynaFrame/version)

(defn df-display [this pane]
  (doto this
    (-> .getContentPane .removeAll)
    (.setContentPane (doto (JPanel.)
		       (.add pane BorderLayout/CENTER)))
    (.pack)
    (.setVisible true)))

(comment
  (def gui (joy.gui.DynaFrame. "4th"))
  (.display gui (doto (javax.swing.JPanel.)
		  (.add (javax.swing.JLabel. "Charlemagne and Pippin."))))
  (.display gui (doto (javax.swing.JPanel.)
		  (.add (javax.swing.JLabel. "Mater semper certa est.")))))

