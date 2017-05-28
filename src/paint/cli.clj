(ns paint.cli
  (:require [clojure.tools.cli :as cli]
            [paint.core :as core]))

(def IMAGE
  "This program side effect is encapsulated only in this atom,
  which contains the current image that is currently being worked on."
  (atom nil))

(def COMMANDS
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
  "Given a list of arguments parse the numbers and the letter
  at the end separately, under the assumption that there can
  only be one final letter after a variable number of numbers"
  [args]
  (if (<= (count args) 2)
    (map parse-int args)
    (let [[pre lst] (split-at (dec (count args)) args)]
      (concat (map parse-int pre) (map keyword lst)))))

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
              (partial core/command op @IMAGE))
        ;; this adjustment is a bit of a hack, but it's just done because
        ;; the :init is the only command that wants numeric arguments
        ;; as they are. All the other arguments get decremented to be
        ;; able to index from 0 instead of from 1
        args-adj (if (= op :init) (map inc args) args)]

    (if (and (nil? @IMAGE) (not= op :init))
      (println "Ignoring command " op " until an image is initialized")
      (reset! IMAGE (apply cmd args-adj)))))

;; (handle-line "I 3 3")

(defn -main
  [& args]
  (doseq [line (line-seq (java.io.BufferedReader. *in*))]
    (println ">" line)
    (handle-line line)))
