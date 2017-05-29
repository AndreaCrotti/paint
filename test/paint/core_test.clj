(ns paint.core-test
  (:require [clojure.test :as t]
            [paint.core :as core]
            [paint.generators :refer [dimension-gen gen-image]]
            [clojure.string :as string]
            [clojure.core.matrix :as matrix]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]))

(defn- rectangular-matrix?
  [img]
  (apply matrix/same-shape? img))

(defn- single-coloured?
  [img colour]
  (every? #(= colour %) (core/pixels img)))

(defspec init-all-white
  100
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init ncols nrows)]
                  (single-coloured? img core/WHITE))))

(defspec init-right-size
  100
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init ncols nrows)]
                  (and
                   (= nrows (count img))
                   (every? #(= ncols %) (map count img))))))


(defspec cleared-img-equal-empty-image-spec
  100
  (prop/for-all
   [image gen-image]
   (let [nrows (-> image matrix/row-count)
         ncols (-> image matrix/column-count)
         cleared (core/command :clear image)]
     (= cleared (core/command :init ncols nrows)))))

(t/deftest img-show-test
  (t/testing "empty image generates matrix of O"
    (let [new-image (core/command :init 2 2)
          shown (core/img-to-string new-image)]

      (t/is (= shown "O O\nO O")))))

(t/deftest command-test
  (let [initial-img (core/command :init 2 2)]
    (t/testing "set single pixel"
      (t/are [x y desired]
          (= (core/command :single-pixel initial-img x y :V) desired)
        0 0 [[:V core/WHITE] [core/WHITE core/WHITE]]
        0 1 [[core/WHITE core/WHITE] [:V core/WHITE]]
        1 1 [[core/WHITE core/WHITE] [core/WHITE :V]]))

    (t/testing "set horizontal line"
      (t/are [x desired]
          (= (core/command :horizontal initial-img 0 1 x :V) desired)
        
        0 [[:V :V] [core/WHITE core/WHITE]]
        1 [[core/WHITE core/WHITE] [:V :V]]))

    (t/testing "set vertical line"
      (t/are [y desired]
          (= (core/command :vertical initial-img 0 1 y :V))
        
        0 [[:V core/WHITE] [:V core/WHITE]]
        1 [[core/WHITE :V] [core/WHITE :V]]))

    (t/testing "fill in region"
      (let [img (core/command :init 3 3)
            filled-in (core/command :fill img 0 0 :V)]
        (t/is (single-coloured? img core/WHITE))
        (t/is (single-coloured? filled-in :V))))))
