(ns ru.nsu.ccfit.g12222.seon.sax-schema
  (:use ru.nsu.ccfit.g12222.seon.core)
  (:use ru.nsu.ccfit.g12222.seon.schema)
  )


(defn initial-state
  "Return initial state of validation automata."
  [schema]
  {:schema (list schema) :valid true :data ()})

; Active schema stack manipulators

(defn- sc
  "Get current active schema from automata state."
  [state]
  (first (:schema state)))

(defn- sc-push
  "Push new active schema into schema stack of automata state."
  [state schema]
  (assoc state
    :schema
    (cons schema (:schema state))))

(defn- sc-pop
  "Remove topmost schema from schema stack."
  [state]
  (assoc state
    :schema
    (next (:schema state))))

; Supplementary data stack manipulation helpers

(defn- data-push
  "Push new frame into data stack."
  [state frame]
  (assoc state :data (cons frame (:data state))))

(defn- data-pop
  "Remove topmost data frame from stack."
  [state]
  (assoc state :data (next (:data state))))

(defn- data
  "Manipulate values on current data frame.

  When called with state only, returns complete data frame.

  When called with state and key arguments, returns value under the key in current frame.

  When called with state, key and value, puts value into current data frame under the key, replacing existing one if any."
  ([state]
   (first (:data state)))

  ([state key]
   (key (data state)))

  ([state key val]
   (data-push
     (data-pop state)
     (assoc (data state) key val))))

(defn data-alter-if-exists
  "Modify data under key in current data frame with passed modifier if key exists."
  [state key modifier]
  (if (not (nil? (data state key)))
    (data state key (modifier (data state key)))
    state))

(defn- v? [state] (:valid state))

(defn- v [state valididty] (assoc state :valid (and (v? state) valididty)))

(defn sax-valid?-atom
  [state value]
  (let [state (v state (valid? (sc state) value)) ; General validation against schema
        state (data-alter-if-exists state ::items inc) ; For list validation
        state (data-alter-if-exists state ::unique #(conj % value))]
    state))

(defn sax-valid?-list-open
  [state]
  (let [schema (sc state)
        type (:type schema "array")
        state (v state (= type "array"))
        nextSchema (if (v? state) (:items schema {}) {})
        state (sc-push state nextSchema)
        newFrame {
                   ::items  (if (or (:maxItems schema) (:minItems schema) (:uniqueItems schema)) 0 nil)
                   ::unique (if (:uniqueItems schema) #{} nil)
                   }
        state (data-push state newFrame)]
    state)
  )

(defn sax-valid?-list-close
  [state]
  (let [state (sc-pop state)
        schema (sc state)

        ; List length validation
        gt? (if (:exclusiveMaximum schema) > >=)
        lt? (if (:exclusiveMaximum schema) < <=)
        state (if (and (data state ::items) (:maxItems schema))
                (v state (gt? (:maxItems schema) (data state ::items)))
                state)
        state (if (and (data state ::items) (:minItems schema))
                (v state (lt? (:minItems schema) (data state ::items)))
                state)

        ; List unique items validation
        state (if (and (data state ::unique) (:uniqueItems schema))
                (v state (= (data state ::items) (count (data state ::unique))))
                state)

        state (data-pop state)
        ]
    state))

(defn sax-valid?-map-open
  [state]
  (let [schema (sc state)
        type (:type schema "object")
        state (v state (= type "object"))
        state (sc-push state {})
        newFrame {
                   ::props (if (:required schema) #{} nil)
                   }
        state (data-push state newFrame)]
    state))

(defn sax-valid?-map-close
  [state]
  (let [state (sc-pop state)

        state (if (and (data state ::props) (:required (sc state)))
                (v state (clojure.set/superset?
                           (data state ::props)
                           (set (:required (sc state)))))
                state)

        state (data-pop state)]
    state))

(defn sax-valid?-map-key
  [state key]
  (let [state (sc-pop state)
        schema (sc state)
        newSchema (or
                    (key (:properties schema))
                    (:additionalProperties schema)
                    {})
        state (sc-push state newSchema)
        state (data-alter-if-exists state ::props #(conj % key))]
    state))

(defn sax-valid?
  "Validate string using sax-parser."
  [schema string]
  (binding
      [*seon-atom* sax-valid?-atom
       *seon-list-open* sax-valid?-list-open
       *seon-list-close* sax-valid?-list-close
       *seon-map-open* sax-valid?-map-open
       *seon-map-close* sax-valid?-map-close
       *seon-map-key* sax-valid?-map-key]
    (v? (seon-sax (initial-state schema) string))))

(defn sax-invalid?
  [schema string]
  (not (sax-valid? schema string)))