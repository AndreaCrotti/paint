(ns paint.cli
  (:require [clojure.tools.cli :as cli]
            [paint.core :as core]))

(def IMAGE (atom nil))

(def COMMANDS
  ;; where could we add the cardinality?
  {"I" :init
   "C" :clear
   "L" :single-pixel
   "V" :vertical
   "H" :horizontal
   "F" :fill
   "S" :show
   "X" :quit})

(def NO-IMG-COMMANDS
  "Commands that don't need the image passed as argument"
  #{:init :quit})

(defn- parse-int
  "This interpreter is using indexes from 1, while core.matrix
  indexes array starting from 0.
  So we just decrement while parsing the argument directly"
  [n]
  (-> n
      Integer/parseInt
      dec))

(defn parse-args
  [args]
  (if (<= (count args) 2)
    (map parse-int args)
    (let [[pre lst] (split-at (dec (count args)) args)]
      (concat (map parse-int pre) lst))))

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
      
      (cond
        (= op :show) (println (core/command :show @IMAGE))
        (= op :init) (reset! IMAGE (apply core/init (map inc args)))
        :else (reset! IMAGE (apply cmd args))))))

(defn -main
  [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (println "> " line)
    (handle-line line)))
