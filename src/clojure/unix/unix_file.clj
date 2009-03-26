; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/20/09)

(ns clojure.unix.unix-file
  (:require [clojure.unix.unix-sys :as u-sys]
	    [clojure.contrib.str-utils :as str-u])
  (:import (java.io File)))

(defn- file-lines
  "Function that returns sequence with the lines of a file"
  [file]
  (str-u/re-split #"\n" (slurp file)))

(defn count-lines
  "Counts the number of lines in a file"
  [file]
  (count (file-lines file)))

(defn map-file
  "Applies function to all line of file"
  [func file]
  (map func (file-lines file)))

(defn newer-than?
  "Determines if file1 is newer than file2"
  [file1 file2]
  (> (.lastModified (File. file1)) (.lastModified (File. file2))))

(defn older-than?
  "Determines if file1 is older than file2"
  [file1 file2]
  (not (newer-than? file1 file2)))