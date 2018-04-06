;; Colombian weather data

;; Copyright (C) 2018 Surreal Turtle

;; Sourcecode designed, written and maintained by
;; Aspasia Beneti <aspra@dyne.org>

;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU Affero General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.

;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU Affero General Public License for more details.

;; You should have received a copy of the GNU Affero General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns colombian-weather-odata.main
  (:require [clojure.string :as str]
            [clojure.java.io :as io]
            [taoensso.timbre :as log])
  (:gen-class))

(defn process-file-by-lines
  "Process file reading it line-by-line"
  ([file]
   (process-file-by-lines file identity))
  ([file process-fn]
   (process-file-by-lines file process-fn println))
  ([file process-fn output-fn]
   (with-open [rdr (io/reader file)]
     (doseq [line (line-seq rdr)]
       (output-fn
        (process-fn line))))))

(defn- parse-date [line]
  line)

(defn- parse-measurments [line]
  (let [body (.substring line 13)
        parts (butlast (loop [b body parts []]
                         (if (= (count b) 0)
                           parts
                           (recur (.substring b 8)
                                  (conj parts (.substring b 0 8))))))
        new-line (reduce str (map #(str % ",") parts))]
    new-line))

(defn parse-hour-label [line]
  (let [parts (str/split (log/spy line) #"\|")]
    (reduce str (map #(str % ",") (rest (rest parts))))))

(defn parser [line]
  (cond
    (re-find #"DEL VIENTO EN SUPERFICIE" line) (parse-date line)
    (re-find #"\| \\ HORA\|" line) (parse-hour-label line)
    (re-find #"^\s{7,8}\d{1,2}" line) (parse-measurments line)))

(defn lazy-file-lines [file]
  (letfn [(helper [rdr]
                (lazy-seq
                 (if-let [line (.readLine rdr)]
                   (if-let [parsed-line (parser line)]
                     (cons parsed-line (helper rdr))
                     (helper rdr))
                    (do (.close rdr) nil))))]
        (helper (clojure.java.io/reader file))))

(defn parse [file]
  (lazy-file-lines file))

(defn col->csv [data]
  (with-open [wrt (io/writer "data.csv")]
  (doseq [line data]
    (.write wrt line)
    (.newLine wrt))))

(defn file->csv [filepath]
  (-> filepath clojure.java.io/resource parse col->csv))

(defn -main [& args]
  (if (= 1 (count args))
    (file->csv (first args))
    (println "To run the program ")))
