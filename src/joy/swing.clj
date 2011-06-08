(ns joy-of-clojure.swing)

(. javax.swing.JOptionPane (showMessageDialog nil "Hello World"))

(comment
  (import '(javax.swing JLabel JFrame))

  (def jlabel (new JLabel "Hello world"))
 
  (def frame (new JFrame))
  (. frame setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)
  (. frame add jlabel)
  (. frame pack)
  (. frame setVisible true)
  (. frame setVisible false))