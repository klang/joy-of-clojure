(ns joy.debugging
  "The breakpoint example from section 13.4.2"
  (:use [joy.macros :only [contextual-eval]]))

(defn div [n d] (int (/ n d)))
;; (div 10 0)
;; no locals in stacktrace

(defn div [n d] (swank.core/break) (int (/ n d)))
;; (div 10 0)
;; locals in stacktrace

(defn readr [prompt exit-code]
  (let [input (clojure.main/repl-read prompt exit-code)]
    (if (= input ::tl) 
      exit-code
      input)))

(comment
  (readr #(print "invisible=> ") ::exit))

(defmacro local-context []
  (let [symbols (keys &env)]
    (zipmap (map (fn [sym] `(quote ~sym)) symbols) symbols)))

(comment
  (let [a 1 b 2 c 3]
    (let [b 200]
      (local-context))))

(defmacro break []
  `(clojure.main/repl
    :prompt #(print "debug=> ")
    :read readr
    :eval (partial contextual-eval (local-context))))

(defn div [n d] (break) (int (/ n d)))
;; (div 10 0)
