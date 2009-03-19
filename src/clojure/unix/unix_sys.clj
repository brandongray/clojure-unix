; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/18/09)
;
; Current ideas:
;; mapfile (fn file-name) : apply function to each line of file
;; lines-in-file (file-name) : count lines in file
;; file-newer? (f1 f2) : is f1 newer than f2?
;; file-size (f1) : size of f1 in bytes

(ns clojure.unix.unix-sys
  (:require [clojure.contrib.shell-out :as shell]))

(defn ext-command [command]
  "Function to pass string as full command to make shell command easier"
    (shell/sh "/bin/sh" "-c" command :return-map true))