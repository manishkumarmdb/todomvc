(ns cljs-boot-starter.client
  (:require [reagent.core :as reagent :refer [atom render]]))

(enable-console-print!)

(def todos (reagent/atom (array-map)))

(def counter (reagent/atom 0))

;; add in todos.........
(defn add-in-todo [text]
  (let [id (swap! counter inc)]
    (swap! todos assoc id (array-map :id id
                                     :todo text
                                     :completed false))))

;; find only vals in todos
(defn todos-all [todos-temp]
  (vals todos-temp))

;; filter active todos
(defn todos-active [todos-temp]
  (let [todos-all (todos-all todos-temp)]
    (filter #(not (:completed %)) todos-all)))

;; filter completed todos thats value is true
(defn todos-completed [todos-temp]
  (let [todos-all (todos-all todos-temp)]
    (filter #(= true (:completed %)) todos-all)))

;; return boolean true if count of todos vals equals count of completed todos
(defn todos-all-completed? [todos-temp]
  (= (count (todos-all todos-temp))
     (count (todos-completed todos-temp))))

;; update todo's :completed key either true or false
(defn todo-update [id]
  (swap! todos update-in [id :completed] not))

;; all todos mark to check true or false
(defn todos-all-update [boolean-val]
  (doseq [todo (todos-all @todos)]
    (swap! todos assoc-in [(:id todo) :completed] (not boolean-val))))

;; application header
(defn header []
  [:div.page-header
   [:h3 "todo application"]])

;; todo-input body take a string and when click on "add" button then it will added in todos
(defn todo-input [text]
  (let [input (reagent/atom text)]
    (fn []
      [:div
       [:input {:type "text"
                :placeholder "add todos"
                :on-change #(reset! input (-> % .-target .-value))
                }] " "
       [:input {:type "button"
                :value "add"
                :on-click #(add-in-todo @input)}]
       ])))

;; define seperate checkbox element for displaying with every todo with todo
(defn todo-checkbox [id]
  (let [check-val (reagent/atom (get-in @todos [id :completed]))]
    [:input {:type "checkbox"
             :on-change #(do
                           (reset! check-val (not (get-in @todos [id :completed])))
                           (todo-update id)
                           (js/console.log (str "id : " id))
                           (js/console.log @check-val))
             :checked @check-val}]))

;; delete todo by id
(defn todo-delete [id]
  (swap! todos dissoc id))

;; delete all completed @todos
(defn delete-all-completed-todos [todos-temp]
  (doseq [todo (todos-completed todos-temp)]
    (todo-delete (:id todo))))

;; this function takes a footer button type and filter it
(defn show-todos [visible-type]
  (case @visible-type
    "all" (vals @todos)
    "active" (filter #(not (:completed %)) (vals @todos))
    "completed" (filter #(= true (:completed %)) (vals @todos))
    "clear-completed" (do
                        (delete-all-completed-todos @todos)
                        (vals @todos))))


;; displaying todos start with checkbox and todo & end with delete button
(defn todos-show [visible-type]
  [:div
   [:table
    [:thead>tr
     [:th
      [:input {:type "checkbox"
               :checked (todos-all-completed? @todos)
               :on-change #(todos-all-update (todos-all-completed? @todos))}]]]
    [:tbody
     (for [todo-get (show-todos visible-type)]
       ^{:key (:id todo-get)}
       [:tr
        [:td
         [todo-checkbox (:id todo-get)]]
        [:td
         (:todo todo-get)]
        [:td
         [:input {:type "button"
                  :value "X"
                  :on-click #(todo-delete (:id todo-get))
                  }]]])]]])

;; dispaly only item or items which calculate on todos count
(defn todos-left [todos-temp]
  (let [todos-count (count (todos-active todos-temp))]
    (str (if (<= 1 todos-count) " item " " items ")
         "left ")))



;; this is footer part of application which dispaly todos state
(defn todos-footer [visible-type]
  [:div
   [:span
    [:bold (count (todos-active @todos))]
    (todos-left @todos)]
   [:span
    [:input {:type "button"
             :value "all"
             :on-click #(reset! visible-type "all")}]] " "
   [:span
    [:input {:type "button"
             :value "active"
             :on-click #(reset! visible-type "active")}]] " "
   [:span
    [:input {:type "button"
             :value "completed"
             :on-click #(reset! visible-type "completed") }]] " "
   [:span
    [:input {:type "button"
             :value "clear-completed"
             :on-click #(reset! visible-type "clear-completed")}]]])

;; this is main application entry point
(defn todomvc []
  (let [visible-data (reagent/atom {})
        visible-type (reagent/atom "all")]
    (fn []
      (let [text (reagent/atom "")]
        [:div
         [:div
          [header]
          [todo-input text]]
         [todos-show  visible-type]
         [:BR]
         [todos-footer visible-type]
         [:p (str @todos)]
         ]))))

(defn init []
  (render [todomvc] (.getElementById js/document "my-app-area")))

(init)
