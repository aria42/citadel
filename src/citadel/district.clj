(ns citadel.district
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.spec.alpha :as s]))

;;; Core data type

(defrecord District
    [title color cost effect])


;; unfortunately need to define a spec
;; to make validation easier
(s/def ::spec
  (s/keys :req-un [::title ::color ::cost ::effect]))


;;; Specs for District fields

(s/def ::title string?)
(s/def ::color #{:green :red :yellow :blue :purple})
(s/def ::cost pos-int?)
;; TODO(aria42): Define turn effects as a set of parameters
;; and each card can update those per player turn or after each play
(s/def ::effect nil?)

;;; Public methods

(defn from-json-str [s]
  (let [district (-> s
                     (json/read-str :key-fn keyword)
                     (update :color keyword)
                     map->District)]
    ;; validate json matches spec and throw along
    ;; with original json for error
    (when-not (s/valid? ::spec district)
      (throw (ex-info "JSON didn't conform to District"
                      {:explain (s/explain-str ::spec district)
                       :json s})))
    district))

(defn load
  "return lazy sequence of `District` cards from readable input.
   will validate each json against `District` spec"
  [input]
  (with-open [rdr (io/reader input)]
    (map from-json-str (line-seq rdr))))
