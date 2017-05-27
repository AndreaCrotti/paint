(ns paint.filling-test
  (:require [paint.filling :as filling]
            [paint.core :refer [WHITE]]
            [clojure.test :as t]))


(t/deftest fill-coordinates-test
  (t/testing "fill coordinates"
    (t/are [img coord new coords]
        (= coords (filling/fill-coordinates img coord new))

      [[WHITE WHITE]
       [WHITE WHITE]] [0 0] :YELLOW #{[0 0] [1 0] [0 1] [1 1]})))
