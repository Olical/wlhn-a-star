(ns wlhn-a-star.core
  (:require [quil.core :as q]
            [quil.middleware :as m]
            [clojure.set :as set]))

;; Warning: HERE BE DRAGONS
;; This is probably the most horrible and messy Clojure I have every written.
;; The reason for that is it was rushed in around an hour, I was learning A* as I went along and the example is extremely imperative.

;; This is extremely inefficient and not idiomatic in any way, you may still find it interesting though.

;; Notes on A* from the Wikipedia page:
;; https://en.wikipedia.org/wiki/A*_search_algorithm

;; We have many nodes.
;; A node has many neighbours.

;; We have a set of nodes we have explored.
;; We have a set of nodes we are yet to explore. Starts with just the first node.

;; We have "came from", mapping nodes to what it can most efficiently be reached from.

;; We have "g score", which is mapping each node to the cost of getting there from the start.
;; The g score defaults to 0 for the first node.

;; We have "f score" which maps nodes to the total cost of getting from the node to the goal. (via this node)
;; The f score defaults to just the first node with a heuristic value. (like, how many nodes we have * 10)

(defn xy->kw [x y]
  (keyword (str x "-" y)))

(defn neighbours [world x y]
  (->> [((xy->kw (dec x) y) world)
        ((xy->kw x (dec y)) world)
        ((xy->kw (inc x) y) world)
        ((xy->kw x (inc y)) world)]
       (into #{} (remove (some-fn nil? #(= :x (:token %)))))))

(defn build-world [src]
  (let [world (->> (flatten
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
                                         (neighbours world x y)))
                                world))))

(def world (build-world [[:s :o :x :e]
                         [:o :o :x :o]
                         [:o :x :x :o]
                         [:o :o :o :o]]))

(defn reconstruct-path [& args]
  (prn "Reconstruct" args))

(defn score [m node]
  (get m node Double/POSITIVE_INFINITY))

(defn distance [{a-x :x, a-y :y} {b-x :x, b-y :y}]
  (+ (Math/abs (- a-x b-x))
     (Math/abs (- a-y b-y))))

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
        (let [current (first (sort-by #(score f-score %) open))]
          (if (= current end)
            (reconstruct-path came-from current)
            (let [open (remove #{current} open)
                  closed (conj closed current)
                  neighbours (remove closed (:neighbours current))
                  {:keys [came-from g-score f-score]} (reduce
                                                       (fn [{:keys [came-from g-score f-score] :as acc} neighbour]
                                                         (let [maybe-g-score (+ (score g-score current) (distance current neighbour))]
                                                           (if (>= maybe-g-score (score g-score neighbour))
                                                             acc
                                                             {:came-from (assoc came-from neighbour current)
                                                              :g-score (assoc g-score neighbour maybe-g-score)
                                                              :f-score (assoc f-score neighbour (+ (score g-score neighbour) (distance neighbour end)))})))
                                                       {:came-from came-from
                                                        :g-score g-score
                                                        :f-score f-score}
                                                       neighbours)]
              (recur closed
                     (set/union open neighbours)
                     came-from
                     g-score
                     f-score))))))))

(def result {:solution (doto (a-star world) prn)})

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
