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
  [:input.toggle {:type "checkbox"
                  ;;:checked completed
                  :on-change #(do
                                (todo-update id)
                                (js/console.log (str "id : " id)))}])

;; delete todo by id
(defn todo-delete [id]
  (swap! todos dissoc id))

;; displaying todos start with checkbox and todo & end with delete button
(defn todos-show []
  [:div
   [:table
    [:thead
     [:input {:type "checkbox"
              :checked (todos-all-completed? @todos)
              :on-change #(todos-all-update (todos-all-completed? @todos))}]
     ;;[:tr [:td "active"] [:td "todos"] [:td "action"]]
     ]
    (for [todo-get (todos-all @todos)]
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
                 }]]])]])

;; dispaly only item or items which calculate on todos count
(defn todos-left [todos-temp]
  (let [todos-count (count (todos-active todos-temp))]
    (str (if (<= 1 todos-count) " item " " items ")
         "left ")))

;;
(defn show-active-todos [todos-temp]
  (todos-active todos-temp))

;;
(defn show-completed-todos [todos-temp]
  (todos-completed todos-temp))

;;
(defn delete-all-completed-todos [todos-temp]
  (doseq [todo (todos-completed todos-temp)]
    (todo-delete (:id todo))))

;; this is footer part of application which dispaly todos state
(defn todos-footer []
  [:div
   [:span
    [:bold (count (todos-active @todos))]
    (todos-left @todos)]
   [:span
    [:input {:type "button"
             :value "all"
             :on-click #(todos-show)}]] " "
   [:span
    [:input {:type "button"
             :value "active"
             :on-click #(show-active-todos @todos)}]] " "
   [:span
    [:input {:type "button"
             :value "completed"
             :on-click #(show-completed-todos @todos)}]] " "
   [:span
    [:input {:type "button"
             :value "clear-completed"
             :on-click #(delete-all-completed-todos @todos)}]]])

;; this is main application part
(defn todomvc []
  (let []
    (fn []
      (let [text (reagent/atom "")]
        [:div
         [:div
          [header]
          [todo-input text]]
         [todos-show]
         [todos-footer]
         ]))))

(defn init []
  (render [todomvc] (.getElementById js/document "my-app-area")))

(init)
