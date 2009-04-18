; This namespace is meant for Unix system related actions
; Brandon Gray (started 3/28/09)

(ns clojure.unix.unix-automation
  (:use (clojure.unix.unix-sys)
	(clojure.contrib.seq-utils)))

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
  ([comment task] (struct list-struct comment (vector task))))

(defn exec-event
  "This function executes an event and returns map with success boolean and output"
  [event]
  (let [output (exec (:command event))]
    {:success (check-exit (:exit output) (:return-codes event)) :out (:out output)}))

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

(defn insert-vec
  "Function to insert item in a vec"
  [my-vec n item]
  (vec
   (concat (take (dec n) my-vec) 
	  (list item) 
	  (drop (dec n) my-vec))))

(defn add-vec-end
  "Function to add item to end of vec"
  [my-vec item]
  (vec (concat my-vec (list item))))

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