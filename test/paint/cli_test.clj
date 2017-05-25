(ns paint.cli-test
  (:require [paint.cli :as cli]
            [clojure.test :as t]))

;; use generative testing also here

(t/deftest parse-command-test
  (t/testing "init command parsed correctly"
    (let [[op args] (cli/parse-command "I 5 6")]
      (t/is (= op :init))
      (t/is (= args [5 6])))))
