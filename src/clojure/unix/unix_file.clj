; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/20/09)

(ns clojure.unix.unix-file
  (:require [clojure.unix.unix-sys :as u-sys]
	    [clojure.contrib.str-utils :as str-u]
	    [clojure.contrib.duck-streams :as duck])
  (:import (java.io File)
	   (java.util.regex Pattern)))

(defn- file-lines
  "Function that returns sequence with the lines of a file"
  [#^String file]
  (str-u/re-split #"\n" (slurp file)))

(defn count-lines
  "Counts the number of lines in a file"
  [#^String file]
  (count (file-lines file)))

(defn map-file
  "Applies function to all line of file"
  [func #^String file]
  (map func (file-lines file)))

(defn line-prefix
  "Function which returns line prefixed if regex is matched"
  [#^Pattern regex #^String line #^String prefix]
  (let [regex-str (.toString regex)]
    (if (re-find regex line)
      (if (re-find (. Pattern compile (str "^" prefix)) line)
	line
	(str prefix line))
      line)))

(defn comment-line
  "Function which returns line commented if regex is matched"
  [#^Pattern regex #^String line]
  (line-prefix regex line "# "))

(defn comment-file
  "Function which writes file based on lines which match regex given"
  [#^String file #^Pattern regex]
  (duck/spit file (str-u/str-join "\n" (map-file #(comment-line regex %) file))))

(defn newer-than?
  "Determines if file1 is newer than file2"
  [#^String file1 #^String file2]
  (let [f1 (File. file1)
	f2 (File. file2)]
    (if (or (not (.exists f1)) (not (.exists f2)))
      nil
      (> (.lastModified (File. file1)) (.lastModified (File. file2))))))

(defn older-than?
  "Determines if file1 is older than file2"
  [#^String file1 #^String file2]
  (let [newer (newer-than? file1 file2)]
    (if (nil? newer)
      nil
      (not newer))))