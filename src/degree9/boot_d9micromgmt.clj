(ns degree9.boot-d9micromgmt
  {:boot/export-tasks true}
  (:require [boot.core         :as boot]
            [boot.pod          :as pod]
            [boot.tmpdir       :as tmpd]
            [boot.util         :as util]
            [clj-yaml.core     :as yaml]
            [clojure.data.json :as json]
            [clojure.string    :as string]
            [clojure.java.io   :as io]
            [cheshire.core     :as cheshire]))

(defn- change-file-ext [path ext]
  (string/replace path #"\.[^\.]+$" (str "." ext)))

(boot/deftask yaml2json
  "Convert YAML files to JSON"
  []
  (let [tmp      (boot/tmp-dir!)
        last-yml (atom nil)]
    (boot/with-pre-wrap fileset
      (boot/empty-dir! tmp)
      (let [yml (->> fileset
                     (boot/fileset-diff @last-yml)
                     boot/input-files
                     (boot/by-ext [".yaml"]))]
        (reset! last-yml fileset)
        (when (seq yml)
          (util/info "Converting YAML files to JSON\n")
          (doseq [f yml]
            (let [in-file  (tmpd/file f)
                  in-path  (tmpd/path f)
                  out-path (change-file-ext in-path "json")
                  out-file (io/file tmp out-path)
                  result (cheshire/generate-string(yaml/parse-string (slurp in-file)))]
              (doto out-path
                io/make-parents
                (spit result))))))
      (-> fileset (boot/add-resource tmp) boot/commit!))))




