(ns paint.cli-test
  (:require [paint.cli :as cli]
            [paint.core :refer [pixels]]
            [clojure.test :as t]))

;; TODO: use generative testing also here

(t/deftest parse-command-test
  (t/testing "init command parsed correctly"
    (let [[op args] (cli/parse-command "I 5 6")]
      (t/is (= op :init))
      (t/is (= args [4 5]))))

  (t/testing "command with colour parsed correctly"
    (let [[op args] (cli/parse-command "F 3 3 J")]
      (t/is (= op :fill))
      (t/is (= args [2 2 :J])))))

(t/deftest handle-line-test
  (t/testing "initialize system"
    (cli/handle-line "I 3 3")
    (t/is (= (count @cli/IMAGE) 3)))

  (t/testing "filling in"
    (cli/handle-line "I 3 3")
    (cli/handle-line "F 1 1 J")
    (t/is (every? #(= :J %) (pixels @cli/IMAGE)))))
