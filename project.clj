(defproject conveyor-web "1.0.0"
  :description "conveyor-web"

  :min-lein-version "2.9.1"

  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/clojurescript "1.10.879"]
                 [com.taoensso/encore "3.19.0"]
                 [reagent "1.1.0" :exclusions [cljsjs/react
                                               cljsjs/react-dom
                                               cljsjs/react-dom-server]]]

  :pedantic? false

  :plugins [[lein-ancient "0.6.15"]
            [lein-kibit "0.1.8"]]

  :source-paths ["src/cljs"]

  :aliases {"fig"         ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build"   ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:staging" ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "staging"]
            "fig:prod"    ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "prod"]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.2.14"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   :resource-paths ["target"]
                   :clean-targets ^{:protect false} ["target" "resources/public/js"]}})
