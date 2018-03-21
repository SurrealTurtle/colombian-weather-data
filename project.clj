(defproject colombian-weather-data "0.1.0"
  :description "Readers for weather data files"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [failjure "1.2.0"] 
                 ;; logging done right with timbre and slf4j
                 [com.taoensso/timbre "4.10.0"]]

  :license {:author "Aspasia Beneti"
            :email "aspra@dyne.org"
            :year 2018
            :key "gpl-3.0"}

  :resource-paths ["resources" "test-resources"]

  :uberjar-name "colombian-weather-data.jar"
  :profiles {:dev {:dependencies [[midje "1.8.3"]]
                   :repl-options {:init-ns colombian.weather.data.main}}})
 
