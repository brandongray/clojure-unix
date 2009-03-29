; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/28/09)

(ns clojure.unix.unix-automation
  (:require [clojure.unix.unix-sys :as u-sys]
	    [clojure.contrib.seq-utils :as seq]))

(defstruct event-struct :comment :command :return-codes)

(defn make-event
  "This function creates an event"
  ([command] (make-event "NA" command '(0)))
  ([comment command] (make-event comment command '(0)))
  ([comment command return-codes] (struct event-struct comment command return-codes)))

(defstruct task-struct :comment :event :pre-task :post-task)

(defn make-task
  "This function creates a task"
  ([event] (make-task "NA" event "NA" "NA"))
  ([comment event] (make-task comment event "NA" "NA"))
  ([event pre-task post-task] (make-task "NA" event pre-task post-task))
  ([comment event pre-task post-task] (struct task-struct comment event pre-task post-task)))

(defstruct list-struct :comment :list)

(defn make-list
  "This function creates a task list"
  ([task] (make-list "NA" task))
  ([comment task] (struct list-struct comment (seq (list task)))))

(defn exec-event
  "This function executes an event and returns map with success boolean and output"
  [event]
  (let [output (u-sys/exec (:command event))]
    {:success (u-sys/check-exit (:exit output) (:return-codes event)) :out (:out output)}))

(defn run-task
  "This function processes / runs a task"
  [task]
  (let [pre-task (:pre-task task)
	event (:event task)
	post-task (:post-task task)]
    (if (= "NA" pre-task)
      (if (:success (exec-event event))
	(if (= "NA" post-task)
	  "DONE"
	  (run-task post-task))
	"ERROR")
      (run-task pre-task))))

(defn insert-seq
  "Function to insert item in a seq"
  [seq n item]
  (concat (take (dec n) seq) 
	  (list item) 
	  (drop (dec n) seq)))

(defn add-seq-end
  "Function to add item to end of seq"
  [seq item]
  (concat seq (list item)))

(defn run-list
  "Function that processes / runs a task list"
  [task-list]
  (let [list (:list task-list)]
    (loop [l list]
      (if (empty? l)
	"DONE"
	(do
	  (run-task (first l))
	  (recur (rest l)))))))