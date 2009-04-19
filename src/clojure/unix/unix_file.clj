; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/20/09)

(ns clojure.unix.unix-file
  (:use [clojure.contrib.str-utils :only (re-split str-join)]
	[clojure.contrib.duck-streams :only (reader write-lines)])
  (:import (java.io File)
	   (java.util.regex Pattern)))

(defn- file-lines
  "Function that returns lazy sequence with the lines of a file"
  [#^String file]
  (line-seq (reader file)))

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

(defn my-write-lines
  "Wrapper for 'clojure.contrib.duck-streams/write-lines using a temp file"
  [f lines]
  (let [temp-file (File/createTempFile "my-temp" nil)
	temp-file-name (.toString temp-file)]
    (do
      (write-lines temp-file-name lines)
      (if (.renameTo temp-file (File. f))
	true
	(do 
	  (.delete temp-file)
	  false)))))

(defn comment-file
  "Function which writes file based on lines which match regex given"
  [#^String file #^Pattern regex]
  (my-write-lines file (map-file #(comment-line regex %) file)))

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