; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/18/09)
;
; Current ideas:
;; (f1 f2) : is f1 newer than f2?
;; file-size (f1) : size of f1 in bytes

(ns clojure.unix.unix-sys
  (:use [clojure.contrib.shell-out :only (sh)]
	[clojure.contrib.seq-utils :only (includes?)])
  (:import (java.io File)))

(defn exec
  "Function to pass string as full command to make shell command easier"
  [#^String command]
  (sh "/bin/sh" "-c" command :return-map true))

(defmacro check-exit 
  "Macro to check if exit code returned by command is valid"
  [#^Integer exit good-exit-list]
  `(if (includes? ~good-exit-list ~exit)
     true false))

(defn move-file
  "Function to move (or rename) file1 to file2"
  [#^String file1 #^String file2]
  (let [output (exec (str "mv " file1 " " file2))]
    (check-exit (:exit output) '(0))))

(defn remove-file
  "Function to remove file"
  [#^String file]
  (let [output (exec (str "rm -rf " file))]
    (check-exit (:exit output) '(0))))
	
(defn list-files
  "Function that returns sequence of files/directories of provided directory"
  [#^String directory]
  (map #(.toString %) (.listFiles (File. directory))))

(defn list-home-files
  "Function that returns sequence of files/directories in users home directory"
  []
  (list-files 
   (System/getProperty "user.home")))

(defn file-exists?
  "Function that tests whether or not a file/directory exists"
  [#^String file]
  (.exists (File. file)))