(defn variable [v]
  (fn [variables]
    (get variables v)))

(defn constant [a]
  (fn [variables] a))

(defn unary [f a]
  (fn [variables]
    (f (a variables))))

(defn binary [f a b]
  (fn [variables]
    (f (a variables) (b variables))))

(defn negate [a]
  (unary #(* % -1) a))

(defn subtract [x1 x2]
  (binary - x1 x2))

(defn pow [x1 x2]
  (binary #(Math/pow %1 %2) x1 x2))

(defn log [x1 x2]
  (binary #(/ (Math/log (Math/abs (double %2))) (Math/log (Math/abs (double %1)))) x1 x2))

(defn multiply [x1 x2]
  (binary * x1 x2))

(defn add [x1 x2]
  (binary + x1 x2))

(defn divide [x1 x2]
  (binary #(/ (double %1) (double %2)) x1 x2))


(def mapping
  {
   '- subtract
   '+ add
   '/ divide
   '* multiply
   'negate negate
   'pow pow
   'log log
   })

(defn checkneg [v]
  (if (clojure.string/starts-with? (str v) "-")
    (negate (variable (str (rest v))))
    (variable (str v))))

(defn checknumber [v]
  (if (< v 0)
    (negate (constant (max v (- v))))
    (constant v)))

(defn my-iterate [coll]
  (let [[f & args] (for [i coll]
                     (cond
                       (list? i)
                       (my-iterate i)

                       (and (symbol? i)
                            (contains? mapping i))
                       (get mapping i)

                       (symbol? i)
                       (checkneg i)

                       (double? i)
                       (checknumber i)))]
    (apply f args)))


(defn parseFunction [input]
  (let [coll (read-string input)]
    (cond
      (list? coll)
      (my-iterate coll)

      (symbol? coll)
      (checkneg coll)

      (number? coll)
      (checknumber coll))
    ))

(def -valid? boolean)

(def -value :value)

(def -tail :tail)

(defn -return [value tail] {:value value :tail tail})

(defn _empty [value] (partial -return value))


(defn _char [p]
  (fn [[c & cs]]
    (if (and c (p c)) (-return c cs))))

(defn _map [f]
  (fn [result]
    (if (-valid? result)
      (-return (f (-value result)) (-tail result)))))


(defn _combine [f a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar)
        ((_map (partial f (-value ar)))
         ((force b) (-tail ar)))))))


(defn _either [a b]
  (fn [str]
    (let [ar ((force a) str)]
      (if (-valid? ar) ar ((force b) str)))))

(defn _parser [p]
  (fn [input]
    (-value ((_combine (fn [v _] v) p (_char #{\u0001})) (str input \u0001)))))



(defn +char [chars] (_char (set chars)))


(defn +char-not [chars] (_char (comp not (set chars))))


(defn +map [f parser] (comp (_map f) parser))

(def +parser _parser)


(def +ignore (partial +map (constantly 'ignore)))


(defn iconj [coll value]
  (if (= value 'ignore) coll (conj coll value)))


(defn +seq [& ps]
  (reduce (partial _combine iconj) (_empty []) ps))


(defn +seqf [f & ps] (+map (partial apply f) (apply +seq ps)))



(defn +seqn [n & ps] (apply +seqf (fn [& vs] (nth vs n)) ps))


(defn +or [p & ps]
  (reduce (partial _either) p ps))


(defn +opt [p]
  (+or p (_empty nil)))

(defn +star [p]
  (letfn [(rec [] (+or (+seqf cons p (delay (rec))) (_empty ())))] (rec)))


(defn +plus [p] (+seqf cons p (+star p)))


(defn +str [p] (+map (partial apply str) p))


(def *digit (+char "0123456789"))

(defn sign [s tail]
  (if (#{\- \+} s)
    (cons s tail)
    tail))

(defn point [s tail]
  (if (#{\.} s)
    (cons s tail)
    tail))
(def *number (+map read-string (+str (+seqf sign (+opt (+char "- +")) (+plus
                                                                        (+str (+seqf point (+opt  (+char ".")) (+plus *digit))))))))
(def *string
  (+seqn 1
         (+char "\"")
         (+str (+star (+char-not "\"")))
         (+char "\"")))

(def *space (+char " \t\n\r"))

(def *ws (+ignore (+star *space)))

(def *all-chars (mapv char (range 32 128)))
(def *letter (+char (apply str (filter #(Character/isLetter %) *all-chars))))
(def *identifier
  (+str (+seqf cons *letter (+star (+or *letter *digit (+char "-"))))))

(def *s-var
  (+map clojure.string/lower-case
        (+map (fn [var-string]
                (let [f (nth var-string 0)]
                  f))
              *identifier)))

(definterface IExpr
  (evaluate [vars])
  (diff [variables])
  (toStringSuffix []))

(defn toStringSuffix [expr]
  (.toStringSuffix expr))

(deftype JConstant [^Number v]
  Object
  (toString [_] (str v))
  IExpr
  (evaluate [_ vars] v)
  (diff [_ variable] (JConstant. 0))
  (toStringSuffix [_] (str v)))

(defn Constant [z] (JConstant. z))



(deftype JVariable [^String v]
  Object
  (toString [_] v)
  IExpr
  (evaluate [_ vars] (double (get vars (-value (*s-var v)))))
  (diff [_ variable] (cond
                       (= (-value (*s-var v)) variable) (JConstant. 1)
                       (not (= (-value (*s-var v)) variable)) (JConstant. 0)))
  (toStringSuffix [_] v))

(defn Variable [x] (JVariable. x))

(deftype JBinary [s f d ^IExpr x ^IExpr y]
  Object
  (toString [_] (str "(" s " " x " " y ")"))
  IExpr
  (evaluate [_ vars] (f vars))
  (diff [_ variables] (d variables))
  (toStringSuffix [_]
    (str "(" (toStringSuffix x) " " (toStringSuffix y) " " s ")")))

(deftype JUnary [s f d ^IExpr x]
  Object
  (toString [_] (str "(" s " " x ")"))
  IExpr
  (evaluate [_ vars] (f vars))
  (diff [_ variables] (d variables))
  (toStringSuffix [_] (str "(" (toStringSuffix x) " " s ")")))

(defn Add [x y] (JBinary.
                  "+"
                  (fn [vars] (+ (.evaluate x vars) (.evaluate y vars)))
                  (fn [variables] (Add (.diff x variables) (.diff y variables)))
                  x y))

(defn Subtract [x y] (JBinary.
                       "-"
                       (fn [vars] (- (.evaluate x vars) (.evaluate y vars)))
                       (fn [variables] (Subtract (.diff x variables) (.diff y variables)))
                       x y))

(defn Multiply [x y] (JBinary.
                       "*"
                       (fn [vars] (* (.evaluate x vars) (.evaluate y vars)))
                       (fn [variables] (Add (Multiply (.diff x variables) y) (Multiply x (.diff y variables))))
                       x y))

(defn Divide [x y] (JBinary.
                     "/"
                     (fn [vars] (/ (double (.evaluate x vars)) (double (.evaluate y vars))))
                     (fn [variables] (Divide
                                       (Subtract (Multiply
                                                   (.diff x variables) y) (Multiply x (.diff y variables)))
                                       (Multiply y y)))
                     x y))

(defn Negate [x] (JUnary.
                   "negate"
                   (fn [vars] (* -1 (.evaluate x vars)))
                   (fn [variables] (Negate (.diff x variables)))
                   x))

(deftype JLn [^IExpr v]
  Object
  (toString [_] (str "(ln " v ")"))

  IExpr
  (evaluate [_ vars] (Math/log (Math/abs(.evaluate v vars))))
  (diff [_ variables]     (cond
                            (clojure.string/includes?  (.toString v) variables) (Multiply (.diff v variables)
                                                                                          (Divide (Constant 1) v) )
                            :else (Constant 0))))

(defn Ln [v] (JLn. v))

(defn Pow [x y] (JBinary.
                  "pow"
                  (fn [vars](Math/pow (double(.evaluate x vars)) (double(.evaluate y vars))))
                  (fn [variables] (cond
                                    (clojure.string/includes?  (.toString x) variables)
                                    (Multiply (.diff x variables)
                                              (Multiply y (Pow
                                                            x (Subtract y (Constant 1)))))
                                    (clojure.string/includes?
                                      (.toString y) variables)
                                    (Multiply (.diff y variables) (Multiply (Pow x y) (Ln x)))
                                    :else (Constant 0)))

                  x y))

(defn Log [x y] (JBinary.
                  "log"
                  (fn [vars] (/ (Math/log (Math/abs (double (.evaluate y vars)))) (Math/log (Math/abs (double (.evaluate x vars))))))
                  (fn [variables]   (cond
                                      (clojure.string/includes?  (.toString y) variables)
                                      (Multiply (.diff y variables)
                                                (Divide (Constant 1) (Multiply y (Ln x))) )
                                      (clojure.string/includes? (.toString x) variables)
                                      (Multiply (.diff x variables)
                                                (Negate (Divide (Ln y) (Multiply x (Pow (Ln x) (Constant 2))))))
                                      :else (Constant 0)))
                  x y))

(defn evaluate [expr vars] (.evaluate expr vars))

(defn toString [expr] (.toString expr))

(defn diff [expr vars] (.diff expr vars))

(def mapping2
  {
   '- Subtract
   '+ Add
   '/ Divide
   '* Multiply
   'negate Negate
   'pow Pow
   'log Log
   })

(defn my-iterate2 [coll]
  (let [[f & args] (for [i coll]
                     (cond
                       (list? i)
                       (my-iterate2 i)

                       (and (symbol? i)
                            (contains? mapping2 i))
                       (get mapping2 i)

                       (symbol? i)
                       (Variable (str i))

                       (number? i)
                       (Constant (double i))))]
    (apply f args)))

(defn parseObject [input]
  (let [coll (read-string input)]
    (cond
      (list? coll)
      (my-iterate2 coll)

      (symbol? coll)
      (Variable (str coll))

      (number? coll)
      (Constant coll))
    ))

(def mapping12
  {\-       Subtract
   \+       Add
   \/       Divide
   \*       Multiply
   "negate" Negate})


(def *operation
  (+char "+-*/"))


(def *s-op
  (+map (fn [op-string]
          (let [f (get mapping12 op-string)]
            f))
        *operation))
(def +neg (+map (constantly  "negate") (+seq (+char "n") (+char "e") (+char "g") (+char "a") (+char "t") (+char "e"))))

(def *s-neg
  (+map (fn [neg-string]
          (let [f (get mapping12 neg-string)]
            f))
        +neg))

(defn apply-suffix-form [[arg1 arg2 op]]
  (op arg1 arg2))
(defn apply-suffix-form-ng [[arg ng]]
  (ng arg))

(def *s-number (+map Constant *number))
(def *s-identifier (+map Variable *identifier))

(def *s-exp
  (+or
    (+seqn 1
           *ws
           (+char "(")
           *ws
           (+map apply-suffix-form-ng
                 (+seq (+or *s-number *s-identifier (delay *s-exp) )
                       *ws
                       *s-neg))
           *ws
           (+char ")")
           *ws)
    (+seqn 1
           *ws
           (+char "(")
           *ws
           (+map apply-suffix-form
                 (+seq (+or *s-number *s-identifier (delay *s-exp) )
                       *ws
                       (+or *s-number  *s-identifier (delay *s-exp))
                       *ws
                       *s-op))
           *ws
           (+char ")")
           *ws)
    (+seqn 0
           *ws
           (+map apply-suffix-form-ng
                 (+seq (+or *s-number  *s-identifier (delay *s-exp) )
                       *ws
                       *s-neg))
           *ws)
    (+seqn 0
           *ws
           (+or *s-number *s-identifier)
           *ws
           )
    ))
(def s-exp
  (+parser *s-exp))
(def parseObjectSuffix s-exp)





