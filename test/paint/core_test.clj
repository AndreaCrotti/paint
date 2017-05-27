(ns paint.core-test
  (:require [clojure.test :as t]
            [paint.core :as core]
            [clojure.string :as string]
            [clojure.core.matrix :as matrix]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))

(def char-to-keyword
  "Transform a char into an uppercased keyword"
  (comp keyword string/upper-case str char))

(def COLOURS (map char-to-keyword (range (int \a) (inc (int \z)))))

(defn- rectangular-matrix?
  [img]
  (apply matrix/same-shape? img))

(defn- all-whites?
  [img]
  (true? (every? #(= core/WHITE %) (core/pixels img))))

(def dimension-gen (gen/choose 1 100))

(def in-empty-img-all-whites
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init ncols nrows)]
                  (all-whites? img))))

(def is-empty-image-right-size
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init ncols nrows)]
                  (and
                   (= nrows (count img))
                   (every? #(= ncols %) (map count img))))))

(defspec init-all-white
  100
  in-empty-img-all-whites)

(defspec init-right-size
  100
  is-empty-image-right-size)

(def gen-image
  (gen/let [[nrows ncols] (gen/vector gen/s-pos-int 2)]
    (gen/vector (gen/vector (gen/elements COLOURS) ncols) nrows)))

(def cleared-img-equal-empty-image
  (prop/for-all
   [image gen-image]
   (let [nrows (-> image matrix/row-count)
         ncols (-> image matrix/column-count)
         cleared (core/command :clear image)]
     (= cleared (core/command :init ncols nrows)))))

(defspec cleared-img-equal-empty-image-spec
  100
  cleared-img-equal-empty-image)

(t/deftest img-show-test
  (t/testing "empty image generates matrix of O"
    (let [new-image (core/command :init 2 2)
          shown (core/command :show new-image)]
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
        1 [[core/WHITE :V] [core/WHITE :V]]))))

;; add an idempotency property, any command
;; can be re-run multiple times and the result won't change
