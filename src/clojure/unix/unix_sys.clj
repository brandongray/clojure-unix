; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/18/09)
;
; Current ideas:
;; mapfile (fn file-name) : apply function to each line of file
;; lines-in-file (file-name) : count lines in file
;; (f1 f2) : is f1 newer than f2?
;; file-size (f1) : size of f1 in bytes

(ns clojure.unix.unix-sys
  (:require [clojure.contrib.shell-out :as shell]
	    [clojure.contrib.seq-utils :as seq]
	    [clojure.contrib.str-utils :as str-u]))

(def *os-info* {:os-type (System/getProperty "os.name")})

(defn exec [command]
  "Function to pass string as full command to make shell command easier"
    (shell/sh "/bin/sh" "-c" command :return-map true))

(defmacro check-exit [exit & good-exit-list]
  "Macro to check if exit code returned by command is valid"
  `(if (seq/includes? '~good-exit-list ~exit)
     true false))

(defn move-file [file1 file2]
  "Function to move (or rename) file1 to file2"
  (let [output (exec (str "mv " file1 " " file2))]
      (check-exit (output :exit) 0)))

(defn remove-file [file]
  "Function to remove file"
  (let [output (exec (str "rm " file))]
    (check-exit (output :exit) 0)))

(defn- get-df-info [fs]
  "Function that returns a vector of df -k"
  (vec (str-u/re-split #"\s+" 
		       (:out (exec (str "df -k" " " fs " " "| grep -v \"^Filesystem\""))))))

; (get-fs-info fs) => (device mount-point total used percent-used)

(defmulti fs-info (fn [os fs] (:os-type os)))

(defmethod fs-info :default (println "Operating System not implemented"))

(defmethod fs-info "Mac OS X" [os fs]
  (let [df-out (get-df-info fs)
	device (df-out 0)
	mount-point (df-out 5)
	total (Integer. (df-out 1))
	used (Integer. (df-out 2))
	percent-used (* (float (/ used total)) 100)]
    (list device mount-point total used percent-used)))

(defn get-fs-info [fs] (fs-info *os-info* fs))