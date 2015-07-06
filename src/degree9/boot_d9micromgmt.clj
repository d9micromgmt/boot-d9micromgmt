(ns degree9.boot-d9micromgmt
  {:boot/export-tasks true}
  (:require [boot.core          :as boot]
            [boot.pod           :as pod]
            [boot.tmpdir        :as tmpd]
            [boot.util          :as util]
            [boot.task.built-in :as tasks]
            [clj-yaml.core      :as yaml]
            [clojure.string     :as string]
            [clojure.java.io    :as io]
            [cheshire.core      :as cheshire]
            [stencil.core       :as stencil]))

(defn- change-file-ext [path ext]
  (string/replace path #"\.[^\.]+$" (str "." ext)))

(defn- convert-file-type
  "Convert files between types."
  [parser generator ext]
   (boot/with-pre-wrap fileset
    (let [tmp      (boot/tmp-dir!)]
            (util/info "Converting files...\n")
            (doseq [f (->> fileset
                       boot/input-files)]
              (let [in-file  (tmpd/file f)
                    in-path  (tmpd/path f)
                    out-path (change-file-ext in-path ext)
                    out-file (io/file tmp out-path)
                    result   (generator (parser (slurp in-file)))]
                (doto out-file
                  io/make-parents
                  (spit result))))
            (-> fileset (boot/add-resource tmp) boot/commit!))))

(defn- yaml-desttype
  "Convert YAML to another type."
  [generator
   ext]
  (convert-file-type boot/yaml-parse generator ext))

(defn- yaml-json
  "Convert YAML to JSON."
  []
  (yaml-desttype boot/json-generate "json"))

(boot/deftask yaml-to-ARM
  "Convert YAML file to Azure Resource Manager JSON file."
  []
  (let [regex #{ #"(?i)(/.*)*AzureResourceManager/.*\.yaml$" }]
    (comp (tasks/sift :include regex)
          (yaml-json)
          (tasks/sift :to-source regex))))

(boot/deftask yaml-to-psdsc
  "Convert YAML file to PowerShell Desired State Configuration."
  []
  (let [regex #{ #"(?i)(/.*)*PSDesiredStateConfiguration/.*\.yaml$" }]
    (comp (tasks/sift :include regex)
          (yaml-json)
          (tasks/sift :to-source regex))))
