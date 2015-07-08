(ns degree9.boot-d9micromgmt.impl
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
            [stencil.loader     :as tmplldr]))

(defn change-file-ext [path ext]
  (string/replace path #"\.[^\.]+$" (str "." ext)))

(defn psdsc-generate
  ""
  [x]
  (stencil/render-file "psdsc.mustache" x))

(defmacro srctype-to-desttype
  ""
  [parser generator ext]
  `(boot/with-pre-wrap fileset#
    (let [tmp# (boot/tmp-dir!)]
      (doseq [f# (->> fileset#
                     boot/input-files)]
        (let [in-file#  (tmpd/file f#)
              in-path#  (tmpd/path f#)
              out-path# (change-file-ext in-path# ~ext)
              out-file# (io/file tmp# out-path#)
              result#   (-> in-file# slurp (~parser :keywords true) doall ~generator)]
          (util/info "Converting files...\n")
          (doto out-file#
            io/make-parents
            (spit result#))))
      (-> fileset# (boot/add-resource tmp#) boot/commit!))))

(defn yaml-json
  "Convert YAML to JSON."
  []
  (srctype-to-desttype yaml/parse-string boot/json-generate "json"))

(defn yaml-psdsc
  "Convert YAML to PowerShell Desired State Configuration."
  []
  (srctype-to-desttype yaml/parse-string psdsc-generate "ps1"))

(defn import-stencil-templates
  ""
  []
  (boot/with-pre-wrap fileset
    (let []
      (doseq [f (->> fileset
                     boot/input-files
                     (boot/by-ext [".mustache"]))]
        (let [in-file (tmpd/file f)
              in-path (tmpd/path f)]
          (util/info "Importing stencil templates...\n")
          (util/info "• %s\n" (.getName in-file))
          (tmplldr/register-template (.getName in-file) (slurp in-file))))
      (-> fileset boot/commit!))))
