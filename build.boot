(set-env!
  :source-paths #{"src"}
  :dependencies '[[org.clojure/clojure       "1.7.0" :scope "provided"]
                  [circleci/clj-yaml         "0.5.3"]
                  [org.clojure/data.json     "0.2.6"]
                  [org.clojure/clojurescript "0.0-3308"]
                  [boot/core                 "2.1.2"]
                  [cheshire                  "5.5.0"]
                  [adzerk/bootlaces          "0.1.11"]
                  [stencil                   "0.4.0"]
                  [circleci/clj-yaml         "0.5.3"]])

(require '[degree9.boot-d9micromgmt :refer :all])
(require '[adzerk.bootlaces         :refer :all])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
  pom {:project 'degree9/boot-d9micromgmt
       :version +version+
       :description "Boot task for converting YAML to JSON."
       :url         "https://github.com/d9micromgmt/boot-d9micromgmt"
       :scm         {:url "https://github.com/d9micromgmt/boot-d9micromgmt"}})

(swap! boot.repl/*default-dependencies*
       concat '[[lein-light-nrepl "0.1.0"]
                [org.clojure/clojurescript "0.0-3308"]])

(swap! boot.repl/*default-middleware*
       conj 'lighttable.nrepl.handler/lighttable-ops)
