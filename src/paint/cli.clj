(ns paint.cli
  (:require [clojure.tools.cli :as cli]
            [paint.core :as core]))

(def IMAGE (atom nil))

(def COMMANDS
  ;; where could we add the cardinality?
  {"I" :init
   "C" :clear
   "L" :set
   "V" :vertical
   "H" :horizontal
   "F" :fill
   "S" :show
   "X" :quit})

(def NO-IMG-COMMANDS
  "Commands that don't need the image passed as argument"
  #{:init :quit})

(defn parse-args
  [args]
  (if (<= (count args) 2)
    (map #(Integer/parseInt %) args)
    (let [[pre lst] (split-at (dec (count args)) args)]
      (concat (map #(Integer/parseInt %) pre) lst))))

(defn parse-command
  "Read a command from string and return its code
  and arguments parsed"
  [line]
  (let [split (clojure.string/split line #" ")
        op (first split)
        args (parse-args (rest split))]

    (when (contains? COMMANDS op)
      [(get COMMANDS op) args])))

(defn handle-line
  "Handle a line and reset the image given the command"
  [line]
  (let [[op args] (parse-command line)
        cmd (if (contains? NO-IMG-COMMANDS op)
              (partial core/command op)
              (partial core/command op @IMAGE))]

    (if (and
         (nil? @IMAGE)
         ;; the IMAGE should always be the first argument passed??
         (not= op :init))

      (println "Ignoring command " op " until an image is initialized")
      
      (if (= op :show)
        (println (core/command :show @IMAGE))
        
        (reset! IMAGE (apply cmd args))))))

;; (handle-line "I 3 3")
;; @IMAGE
;; (handle-line "C")

;; (handle-line "L 0 2 A")
;; (apply (partial core/command :set @IMAGE) [0 2 "A"])
;; (core/command :set @IMAGE 0 0 "A")
;; (handle-line "H 0 1 0 B")

(defn -main
  [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (println "> " line)
    (handle-line line)))
