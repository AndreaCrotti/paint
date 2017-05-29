(ns paint.filling
  (:require [clojure.core.matrix :as matrix]
            [clojure.set :refer [union]]))

(defn valid-coord?
  "Check if the given coordinate is valid, starting
  the indexing from 1 and not from 0"
  [[x y] img]
  (and (>= x 0) (>= y 0)
       (<= x (matrix/column-count img))
       (<= y (matrix/row-count img))))

(def directions
  {:north [dec identity]
   :east [identity inc]
   :south [inc identity]
   :west [identity dec]})

(defn move
  "Transform the coordinate to the given cardinal direction"
  [direction coord]
  (mapv
   (fn [func c] (func c)) (direction directions) coord))

(defn fill-coordinates
  "Recursive function that generates all the coordinates
  which need to be filled in with the new colour.
  It's inspired by the flood-fill algorithm, but had to be
  heavily modified since there is no mutation here.

  It's important to keep in mind that this will potentially
  generate a stack overflow if the matrix is too big, so
  use it at your own risk (using flood-fill on a mutable
  data structure would be much more performant anyway)."

  ([img coord old-colour new-colour coords]
   (if (or (= new-colour old-colour)
           ;; when outside of the range no need for the extra check
           (not (valid-coord? coord img)))
     coords
     (if (or
          (contains? coords coord)
          (not= (get-in img coord) old-colour))
       coords
       (letfn [(rec-call [direction filled-coords]
                 (fill-coordinates img (move direction coord) old-colour new-colour filled-coords))]

         (let [c-incl (union coords #{coord})
               c-north (rec-call :north c-incl)
               c-east (rec-call :east c-north)
               c-south (rec-call :south c-east)]

           (rec-call :west c-south))))))

  ([img coord new-colour]
   (let [old-colour (get-in img coord)]
     (fill-coordinates img coord old-colour new-colour #{}))))
