(ns paint.core
  (:require [clojure.core.matrix :as matrix]
            [clojure.string :as string]
            [clojure.set :refer [union]]
            [paint.filling :refer [fill-coordinates]]))

(def WHITE :O)

(defn pixels
  "Return all the pixels as a simple list of colours"
  [img]
  (apply concat img))

(defmulti command
  (fn [cmd & args] cmd))

(defmethod command :clear
  [_ img]
  (matrix/matrix
   (matrix/fill img WHITE)))

(defmethod command :init
  [_ ncols nrows]
  (command :clear (matrix/zero-matrix nrows ncols)))

(defmethod command :region
  [_ img xs ys colour]
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

(defn img-to-string
  [img]
  (clojure.string/join
   "\n"
   (map #(clojure.string/join " " (map name %)) img)))

(defmethod command :show
  [_ img]
  (println (img-to-string img))
  img)

;; could use :default as well here potentially?
(defmethod command :unknown
  [_]
  (println "Ignoring unnknown command"))

(defmethod command :quit
  [_]
  (System/exit 0))
