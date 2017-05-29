(defproject paint "0.1.0-SNAPSHOT"
  :description "Abstract image painter"
  :url "https://github.com/AndreaCrotti/paint"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0-alpha17"]
                 [net.mikera/core.matrix "0.60.3"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/test.check "0.9.0"]]

  :plugins [[lein-cloverage "1.0.9"]]
  :main paint.cli)
