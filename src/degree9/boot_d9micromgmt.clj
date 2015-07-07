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
            [stencil.core       :as stencil]
            [degree9.boot-d9micromgmt.impl :as impl]))

(boot/deftask yaml-to-ARM
  "Convert YAML file to Azure Resource Manager JSON file."
  []
  (let [regex #{ #"(?i)(/.*)*AzureResourceManager/.*\.yaml$" }]
    (comp (tasks/sift :to-source regex :include regex)
          (impl/yaml-json))))

(boot/deftask yaml-to-psdsc
  "Convert YAML file to PowerShell Desired State Configuration."
  []
  (let [regex #{ #"(?i)(/.*)*PSDesiredStateConfiguration/.*\.yaml$" }]
    (comp (impl/import-stencil-templates)
          (tasks/sift :to-source regex :include regex)
          (impl/yaml-psdsc))))
