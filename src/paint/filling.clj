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
  [direction coord]
  (vec
   (let [funcs (direction directions)]
     (for [[f v] (zipmap funcs coord)]
       (f v)))))

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
             c-north (fill-coordinates img (move :north coord) old-colour new-colour c-incl)
             c-east (fill-coordinates img (move :east coord) old-colour new-colour c-north)
             c-south (fill-coordinates img (move :south coord) old-colour new-colour c-east)
             c-west (fill-coordinates img (move :west coord) old-colour new-colour c-south)]
         c-west))))

  ([img coord new-colour]
   (let [old-colour (get-in img coord)]
     (fill-coordinates img coord old-colour new-colour #{}))))
