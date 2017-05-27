(ns paint.core
  (:require [clojure.core.matrix :as matrix]
            [clojure.string :as string]))

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

(defmethod command :set
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
