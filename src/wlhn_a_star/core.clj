(ns wlhn-a-star.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.set :as set]))

;; We have many nodes.
;; A node has many neighbours.

;; We have a set of nodes we have explored.
;; We have a set of nodes we are yet to explore. Starts with just the first node.

;; We have "came from", mapping nodes to what it can most efficiently be reached from.

;; We have "g score", which is mapping each node to the cost of getting there from the start.
;; The g score defaults to 0 for the first node.

;; We have "f score" which maps nodes to the total cost of getting from the node to the goal. (via this node)
;; The f score defaults to just the first node with a heuristic value. (like, how many nodes we have * 10)

(def infinity 10000000)

(defn xy->kw [x y]
  (keyword (str x "-" y)))

(defn build-world [src]
  (let [grid (->> (flatten
                   (map-indexed
                    (fn [y row]
                      (map-indexed
                       (fn [x token]
                         {:x x
                          :y y
                          :token token})
                       row))
                    src))
                  (reduce
                   (fn [acc {:keys [x y] :as node}]
                     (assoc acc (xy->kw x y) node))
                   {}))]
    (remove #(= :x (:token %)) (map
                                (fn [[_ {:keys [x y] :as node}]]
                                  (assoc node
                                         :neighbours
                                         (->> [((xy->kw (dec x) y) grid)
                                               ((xy->kw x (dec y)) grid)
                                               ((xy->kw (inc x) y) grid)
                                               ((xy->kw x (inc y)) grid)]
                                              (remove nil?)
                                              (into #{}))))
                                grid))))

(def world (build-world [[:s :o :x :e]
                         [:o :o :x :o]
                         [:o :x :x :o]
                         [:o :o :o :o]]))

(defn reconstruct-path []
  :???)

(defn a-star [world]
  (let [start (first (filter #(= :s (:token %)) world))
        end (first (filter #(= :e (:token %)) world))]
    (loop [closed #{}
           open #{start}
           came-from {}
           g-score {start 0}
           f-score {start (* (count world) 10)}]
      (if (empty? open)
        :failure
        (let [current (first (sort-by #(get f-score %) open))]
          (if (= current end)
            (reconstruct-path came-from current)
            (let [open (remove #{current} open)
                  closed (conj closed current)
                  neighbours (remove closed (:neighbours current))
                  tent-g-score (inc (get g-score current infinity))]
              (recur closed
                     (set/union open neighbours)
                     came-from
                     g-score
                     f-score))))))))

(a-star world)

(def result {:solution (a-star world)})

(defn setup []
  (q/frame-rate 30)
  {})

(defn update-state [state]
  result)

(defn draw-state [state]
  (q/background 255))

(comment
  (q/defsketch wlhn-a-star
    :title "West London Hack Night A*"
    :size [500 500]
    :setup setup
    :update update-state
    :draw draw-state
    :features [:keep-on-top]
    :middleware [m/fun-mode]))
