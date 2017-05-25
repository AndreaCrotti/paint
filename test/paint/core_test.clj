(ns paint.core-test
  (:require [clojure.test :as t]
            [paint.core :as core]
            [clojure.core.matrix :as matrix]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.clojure-test :refer [defspec]]))

(defn- rectangular-matrix?
  [img]
  (apply matrix/same-shape? img))

(defn- all-whites?
  [img]
  (true? (every? #(= core/WHITE %) (core/vals img))))

(def dimension-gen (gen/choose 1 100))

(def in-empty-img-all-whites
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init nrows ncols)]
                  (all-whites? img))))

(def is-empty-image-right-size
  (prop/for-all [ncols dimension-gen nrows dimension-gen]
                (let [img (core/command :init nrows ncols)]
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
    (gen/vector (gen/vector (gen/elements core/COLOURS) ncols) nrows)))

(def cleared-img-equal-empty-image
  (prop/for-all
   [image gen-image]
   (let [nrows (-> image matrix/row-count)
         ncols (-> image matrix/column-count)
         cleared (core/command :clear image)]
     (= cleared (core/command :init nrows ncols)))))

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
          (= (core/command :set initial-img x y :V) desired)
        0 0 [[:V :O] [:O :O]]
        1 1 [[:O :O] [:O :V]]))

    (t/testing "set horizontal line"
      (t/are [x desired]
          (= (core/command :horizontal initial-img x 0 1 :V) desired)
        
        0 [[:V :V] [:O :O]]
        1 [[:O :O] [:V :V]]))

    (t/testing "set vertical line"
      (t/are [y desired]
          (= (core/command :vertical initial-img 0 1 y :V))

        0 [[:V :O] [:V :O]]
        1 [[:O :V] [:O :V]]))))

;; add an idempotency property, any command
;; can be re-run multiple times and the result won't change
