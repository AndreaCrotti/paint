(ns paint.core
  (:require [clojure.core.matrix :as matrix]
            [clojure.string :as string]
            [clojure.math.combinatorics :refer [cartesian-product]]))

(def WHITE :O)
(def char-to-keyword
  "Transform a char into an uppercased keyword"
  (comp keyword string/upper-case str char))

(def COLOURS (map char-to-keyword (range (int \a) (inc (int \z)))))

(defn pixels
  [img]
  (apply concat img))

(defmulti command
  (fn [cmd & args] cmd))

(defmethod command :init
  [_ ncols nrows]
  (command :clear (matrix/zero-matrix nrows ncols)))

(defmethod command :clear
  [_ img]
  (matrix/matrix
   (matrix/fill img WHITE)))

(defmethod command :region
  [_ img xs ys colour]
  ;; should we use core.matrix for this or not?
  (matrix/matrix
   (matrix/set-selection img (map dec ys) (map dec xs) colour)))

(defmethod command :fill
  [_ img x y colour]
  [])

(defmethod command :single-pixel
  [_ img x y colour]
  (command :region img [x x] [y y] colour))

(defmethod command :horizontal
  [_ img x1 x2 y colour]
  (command :region img [x1 x2] [y y] colour))

(defmethod command :vertical
  [_ img x y1 y2 colour]
  (command :region img [x x] [y1 y2] colour))

(defmethod command :show
  [_ img]
  (clojure.string/join
   "\n"
   (map #(clojure.string/join " " (map name %)) img)))

(defmethod command :quit
  [_]
  (System/exit 0))

(def OPS [inc dec identity])

(defn valid-coord?
  "Check if the given coordinate is valid, starting
  the indexing from 1 and not from 0"
  [[x y] img]
  (and (>= x 1) (>= y 1)
       (<= x (matrix/column-count img))
       (<= y (matrix/row-count img))))

(defn neighbour-coordinates
  "Return all the neighbour coordinates by doing a cartesian product
  on the functions that need to be applied an remove the coordinated
  passed in"
  [[x y] board]
  (sort
   (filter #(not= % [x y])
           (for [ops (cartesian-product OPS OPS)]
             ((apply juxt ops) x)))))
