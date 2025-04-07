(defn v+ [[& a] [& b]]
  (mapv + a b))

(defn v* [[& a] [& b]]
  (mapv * a b))

(defn v- [[& a] [& b]]
  (mapv - a b))

(defn vd [[& a] [& b]]
  (mapv / a b))
; :NOTE: копипаста в векторных функциях выше
(defn scalar [[& a] [& b]]
  (apply + (mapv * a b)))

(defn vect [a, b] (let [[x y z] a [q w t] b]
                    (vector
                      (- (* y t) (* z w)),
                      (- (* z q) (* x t)),
                      (- (* x w) (* y q)))))

(defn v*s [[& a] b] (mapv (fn [s] (* s b)) a))

(defn m+ [[& a] [& b]]
  (mapv v+ a b))

(defn m* [[& a] [& b]]
  (mapv v* a b))

(defn m- [[& a] [& b]]
  (mapv v- a b))

(defn md [[& a] [& b]]
  (mapv vd a b))

; :NOTE: функции громоздкие и похожи на копипасту
(defn transpose [[& a]]
  (letfn[(transp [n r]
           (if (< n (count (nth a 0)))
             (recur
               (inc n)
               (conj r
                     (vec (reduce concat
                                  (map #(conj [] (nth % n))
                                       a
                                       )))
                     )
               )
             r
             ))]
    (transp 0 []) ; :NOTE: неявный for из императивного стиля
    )
  )

(defn m*s [[& a] b]
  (letfn[(umnoj [n r]
           (if (< n (count a))
             (recur
               (inc n)
               (conj r (mapv #(* b %) (nth a n)))
               )
             r
             ))]
    (umnoj 0 [])
    ) ; :NOTE: неявный for из императивного стиля
  )


(defn m*v [[& m] [& v]]
  (letfn[(umnoj [n r]
           (if (< n (count m))
             (recur
               (inc n)
               (conj r (apply +(mapv * (nth m n) v)))
               )
             r
             ))]
    (umnoj 0 [])
    )
  )

(defn m*m [[& a] [& b]]
  (def c (transpose b))
  (letfn[(umnoj [n r]
           (if (< n (count a))
             (recur
               (inc n)
               (conj r (m*v c (nth a n) )  )
               )
             r
             ))]
    (umnoj 0 [])
    )
  )
(defn c+ [[& a] [& b]]
  (mapv m+ a b))

(defn c- [[& a] [& b]]
  (mapv m- a b))

(defn c* [[& a] [& b]]
  (mapv m* a b))
(defn cd [[& a] [& b]]
  (mapv md a b))

