; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/20/09)

(ns clojure.unix.unix-filesystem
  (:use (clojure.unix.unix-sys)
	(clojure.contrib.str-utils)))

; Global containing operating system related information
(def *os-info* {:os-type (System/getProperty "os.name")})

(defn string->int
  "Function that turns a String into an Integer"
  [#^String num-str]
  (try
   (Integer/parseInt num)
   (catch NumberFormatException ne)))

(defn- get-df-info
  "Function that returns a vector of df -k"
  [#^String fs]
  (vec (re-split #"\s+" 
		       (:out (exec (str "df -k " fs " | grep -v \"^Filesystem\""))))))

(defmulti fs-info
  "Function to get filesystem information"
  (fn [fs] (:os-type *os-info*)))

(defmethod fs-info :default [#^String fs] (println "Operating System not implemented"))

(defmethod fs-info "Mac OS X" [#^String fs]
  (let [df-out (get-df-info fs)
	device (df-out 0)
	mount-point (df-out 5)
	total (string->int (df-out 1))
	used (string->int (df-out 2))
	percent-used (* (float (/ used total)) 100)]
    (list device mount-point total used percent-used)))

(defmulti fs-list
  "Function to gather list of filesystems"
  (fn [] (:os-type *os-info*)))

(defmethod fs-list "Mac OS X" []
  (vec (re-split #"\n"
		       (:out (exec (str "mount | grep hfs | awk '{print $3}'"))))))