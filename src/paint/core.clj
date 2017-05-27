(ns paint.core
  (:require [clojure.core.matrix :as matrix]
            [clojure.string :as string]
            [clojure.set :refer [union]]
            [clojure.math.combinatorics :refer [cartesian-product]]))

(def WHITE :O)
(def char-to-keyword
  "Transform a char into an uppercased keyword"
  (comp keyword string/upper-case str char))

(def COLOURS (map char-to-keyword (range (int \a) (inc (int \z)))))

(defn valid-coord?
  "Check if the given coordinate is valid, starting
  the indexing from 1 and not from 0"
  [[x y] img]
  (and (>= x 0) (>= y 0)
       (<= x (matrix/column-count img))
       (<= y (matrix/row-count img))))

(defn- north
  [[x y]]
  [(dec x) y])

(defn- east
  [[x y]]
  [x (inc y)])

(defn- south
  [[x y]]
  [(inc x) y])

(defn- west
  [[x y]]
  [x (dec y)])

(defn fill-coordinates
  ([img coord old-colour new-colour coords]
   (if (or (= new-colour old-colour)
           ;; when outside of the range no need for the extra check
           (not (valid-coord? coord img)))
     coords
     (if (or
          (contains? coords coord)
          (not= (get-in img coord) old-colour))
       ;; base step of the induction, 
       coords
       (let [c-incl (union coords #{coord})
             c-north (fill-coordinates img (north coord) old-colour new-colour c-incl)
             c-east (fill-coordinates img (east coord) old-colour new-colour c-north)
             c-south (fill-coordinates img (south coord) old-colour new-colour c-east)
             c-west (fill-coordinates img (west coord) old-colour new-colour c-south)]
         c-west))))

  ([img coord new-colour]
   (let [old-colour (get-in img coord)]
     (fill-coordinates img coord old-colour new-colour #{}))))

(defn pixels
  [img]
  (apply concat img))

(defmulti command
  (fn [cmd & args] cmd))

(defmethod command :clear
  [_ img]
  (matrix/matrix
   (matrix/fill img WHITE)))

(defn init
  [ncols nrows]
  (command :clear (matrix/zero-matrix nrows ncols)))

(defmethod command :region
  [_ img xs ys colour]
  ;; should we use core.matrix for this or not?
  (matrix/matrix
   (matrix/set-selection img ys xs colour)))

(defmethod command :fill
  [_ img x y colour]
  (let [indices (fill-coordinates img [x y] colour)]
    (matrix/set-indices img indices colour)))

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
