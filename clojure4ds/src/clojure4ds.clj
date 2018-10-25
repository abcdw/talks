(ns clojure4ds
  (:require [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Learn the Clojure

;; REPL driven

;; types and collections
'(1 2/3 4.5 "three" :four)

[5 6 7]

{:key        "value"
 "other-key" 341}

#{1 :a :b}

(def simple-symbol {:a :b, :c :d})

(seq [234 3 2])
(seq simple-symbol)

;; functions

(+ 3 4)

(defn my-sum [a b]
  (+ a b))

(my-sum 3 5)

((fn [b] (+ 4 b)) 10)

(#(+ 4 %) 10)

(#(+ 4 %1 %2) 10 12)

(:test {:test :value})

(#{:a :b} :a)

;; macros

(-> {:a 10}
    (get :a)
    (+ 15))

(macroexpand
 '(-> {:a 10}
      (get :a)
      (+ 15)))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Java interop | Read the csv

(defn read-csv [filename]
  (with-open [reader (io/reader filename)]
    (doall
     (csv/read-csv reader))))

(def events-data
  (read-csv "/home/abcdw/work/mlcourse.ai/data/athlete_events.csv"))

#spy/p
(take 10 events-data)

(count events-data)

(def header #spy/p (mapv keyword (first events-data)))
(def events-rows (rest events-data))

#spy/p
(take 10 events-rows)

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Map this | DataFrame

(zipmap [:a :b :c] [1 2 3])

(map + [1 2 3] [5 6 7])

(map zipmap
     (->> header
          repeat)
     events-rows)

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data)
            (map keyword)
            repeat)
       (rest csv-data)))

(def dirty-events
  (csv-data->maps events-data))

#spy/p
(first dirty-events)

(->> dirty-events
     (filter #(= "NA" (:Age %)))
     count)


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Coerce the data

(defn to-int [s]
  (if (= "NA" s)
    nil
    (Integer. s)))

(to-int "NA")
(to-int "135")

(defn to-float [s]
  (if (= "NA" s)
    nil
    (Float. s)))

(defn coerce-athlete [athlete]
  (-> athlete
      (update :Age to-int)
      (update :Weight to-float)
      (update :Height to-int)))

(coerce-athlete
 {:Age "14"
  :Name "name"
  :Weight "10"
  :Height "50"})

(def events
  (map coerce-athlete dirty-events))

(->> events
     (filter #(some? (:Age %)))
     (filter #(> 15 (:Age %)))
     count)

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Rotate the data | Series

;; (count
;;  (into [] (comp (map :Age) (filter some?)) events))

(->>
 (map :Age events)
 ;; (filter nil?)
 ;; count
 )

(->>
 (map :Age events)
 (filter some?)
 (reduce +))

(def minimal-age
  (->>
   (map :Age events)
   (filter some?)
   (reduce min)
   ;; (reduce max)
   ))

(defn aggregate [f key data]
  (->>
   (map key data)
   (filter some?)
   (reduce f)))

(defn get-minimal-age [events]
  (aggregate min :Age events))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Who is the youngest?

(->>
 events
 (filter #(some? (:Age %)))
 (reduce #(if (< (:Age %2) (:Age %1)) %2 %1))
 ;; (reduce max)
 )

(->> events
     (filter #(= minimal-age (:Age %))))

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Yongest of 1996


(->> events
     (filter #(= "1996" (:Year %)))
     (filter #(= "M" (:Sex %)))
     count
     ;; get-minimal-age
     )

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Filter applier

(defn filter-applier [filters coll]
  (reduce (fn [acc f]
            (filter f acc))

          coll filters))

(def filters [#(= "1996" (:Year %))
              #(= "M" (:Sex %))])

(->> events
     (filter-applier filters)
     count)


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Transducers

(def xf (comp (filter #(= "1996" (:Year %)))
           (filter #(= "M" (:Sex %)))))

(count
 (sequence xf events))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Gymnastics 2000


#spy/p
(->> (group-by :Name events)
     count
     ;; first
     )

(->> (group-by :Name events)
     (map second)                       ; vals
     (map first)
     (map #(select-keys % [:Sex :Sport :Year])))

(defn drop-duplicates [column data]
  (->> (group-by column data)
       (map second)                       ; vals
       (map first)))

(def uniq-events
  (drop-duplicates :Name events))

;; https://github.com/pandas-dev/pandas/blob/v0.23.4/pandas/core/frame.py#L4361

(def man-filters [#(= "2000" (:Year %))
                  #(= "M" (:Sex %))])

(def gym-man-filters (into man-filters
                           [#(= "Gymnastics" (:Sport %))]))

(def ratio
  (/
   (->> uniq-events
        (filter-applier gym-man-filters)
        count)
   (->> uniq-events
        (filter-applier man-filters)
        count)))

(float (* ratio 100))


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Mean and standard deviation


(defn filter-generator [m]
  (let [filters (mapv (fn [[f v]]
                        #(= v (f %))) m)]
    (fn [coll]
      (filter-applier filters coll))
    ))

(def basket-girl-filter
  (filter-generator
   {:Year "2000"
    :Sex "F"
    :Sport "Basketball"
    ;; #(< (:Age %) 18) true
    }))

(->> uniq-events
     basket-girl-filter)

(defn mean [coll] (/ (reduce + coll) (count coll)))

(defn square [n] (* n n))

(defn standard-deviation [coll]
  (Math/sqrt
   (/
    (->>
     (map - coll (repeat (mean coll)))
     (map square)
     (reduce + ))
    (dec (count coll)))))

(->> events
     basket-girl-filter
     (drop-duplicates :Name)
     (map :Height)
     mean
     float)

(->> events
     basket-girl-filter
     (drop-duplicates :Name)
     (map :Height)
     standard-deviation
     float)

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Highest weight

(def events-2002
  (->> events
   (filter #(= "2002" (:Year %)))
   (drop-duplicates :Name)))

(def mx-weight
  (->> events-2002
       (aggregate max :Weight)))

(def mx-filter
  (filter-generator {:Weight mx-weight}))

(-> events-2002
    mx-filter
    first
    :Sport)


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Pawe Abratkiewicz

(def pawe-filter
  (filter-generator
   {:Name "Pawe Abratkiewicz"}))

(->> events
     pawe-filter
     (drop-duplicates :Year)
     count)


;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  medals in tennis did Australia win

(def tennis-filter
  (filter-generator
   {:Year "2000"
    :Sport "Tennis"
    :Team "Australia"
    :Medal "Silver"}))

(->> events
     tennis-filter
     count)

;; ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;  Serbia vs Switzerland

(def filters
  [(filter-generator
    {:Year                   "2016"
     :Team                   "Switzerland"
     #(not= "NA" (:Medal %)) true})

   (filter-generator
    {:Year                   "2016"
     :Team                   "Serbia"
     #(not= "NA" (:Medal %)) true})])

(mapv #(%1 %2)
      filters (repeat events))

(apply <
 (mapv #(count (%1 %2))
       filters (repeat events)))
