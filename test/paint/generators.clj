(ns paint.generators
  (:require [clojure.test.check.generators :as gen]
            [clojure.string :as string]))

(def char-to-keyword
  "Transform a char into an uppercased keyword"
  (comp keyword string/upper-case str char))

(def COLOURS (map char-to-keyword (range (int \a) (inc (int \z)))))

(def dimension-gen (gen/choose 1 100))

(def gen-image
  (gen/let [[nrows ncols] (gen/vector gen/s-pos-int 2)]
    (gen/vector (gen/vector (gen/elements COLOURS) ncols) nrows)))
