(ns ^:figwheel-hooks conveyor-web.core
  (:require
   [clojure.string :as s]
   [reagent.core :as r]
   [reagent.dom :as rd]
   [taoensso.encore :refer [ajax-lite]]
   [conveyor-web.ui :as ui]))

(goog-define ^boolean DEBUG true)
(goog-define REQUEST-URL "")

(defonce app-state (r/atom {:date (js/moment)
                            :date-str (. (js/moment) format "DD.MM.YYYY")
                            :data nil
                            :auto-reload? true
                            :page-status nil
                            :loading? false}))

(defn format-number
  "Formats NUMBER using LOCALE"
  [number locale]
  (.toLocaleString number locale))

(defn current-date-string
  "Get current date string using Moment.js and formatted by FORMAT-STRING"
  [format-string]
  (. (js/moment) format format-string))

(defn page-status
  [status]
  (case status
    :success {:status "success"
              :text (str "данные обновлены в "
                         (current-date-string "HH:mm:ss"))}
    :error {:status "error"
            :text (str "ошибка при выполнении запроса в "
                       (current-date-string "HH:mm:ss"))}
    :processing {:status "processing"
                 :text "выполняется обновление данных"}
    {:status "error"
     :text (str "неизвестная ошибка в "
                (current-date-string "HH:mm:ss"))}))

(defn fetch-data
  [uri request-date]
  (ajax-lite uri
             {:method :get
              :params {:request-date request-date}
              :headers {"Accept" "application/edn"}
              :resp-type :edn
              :timeout-ms 30000}
             (fn [{:keys [success? ?content]}]
               (if success?
                 (swap! app-state
                        assoc
                        :data ?content
                        :loading? false
                        :page-status (page-status :success))
                 (swap! app-state
                        assoc
                        :loading? false
                        :page-status (page-status :error))))))

(defn update-data!
  ([] (update-data! (js/moment) (current-date-string "DD.MM.YYYY")))
  ([date date-str] (swap! app-state
                          assoc
                          :date date
                          :date-str date-str
                          :loading? true
                          :page-status (page-status :processing))
                   (fetch-data REQUEST-URL (:date-str @app-state))))

(defn stop-job!
  []
  (swap! app-state assoc :job (js/clearInterval (:job @app-state))))

(defn start-job!
  []
  (stop-job!)
  (swap! app-state assoc :job (js/setInterval #(update-data!) 600000)))

(defn show-bar-chart
  [data]
  (let [context (.getContext (.getElementById js/document "bar-chart") "2d")
        chart-data {:type "bar"
                    :data {:labels (map (fn [{:keys [date-end]}]
                                          (last (s/split date-end #" ")))
                                        data)
                           :datasets [{:data (map :hour-weight data)
                                       :label "Расход, т/ч"
                                       :borderWidth 1
                                       :borderColor "rgb(24, 144, 255)"
                                       :backgroundColor "rgba(24, 144, 255, .3)"}]}
                    :options {:scales {:y {:beginAtZero true}}}}]
    (js/chart. context (clj->js chart-data))))

(defn bar-chart
  [data]
  (r/create-class
   {:component-did-mount #(show-bar-chart data)
    :display-name "bar-chart"
    :reagent-render (fn [] [:canvas {:id "bar-chart"}])}))

(defn view
  []
  [ui/config-provider {:locale (goog.object/get js/antd "locale")}
   [ui/backtop]
   [ui/modal {:visible (:modal? @app-state)
              :title (:modal-title @app-state)
              :onOk #(swap! app-state assoc :modal? false)
              :onCancel #(swap! app-state assoc :modal? false)
              :destroyOnClose true
              :centered true
              :footer nil
              :width 1440}
    [bar-chart (filter #(= (:tag @app-state) (:tag %)) (:data @app-state))]]
   [ui/layout {:style {:min-height "100vh"}}
    [ui/layout-content {:style {:padding "24px"}}
     [:div {:style {:padding "24px"
                    :background "#fff"}}
      [ui/row {:justify "space-between"
               :align "middle"}
       [ui/col
        [ui/typography-title {:level 2}
         (str "Весы конвейеров" ", " (:date-str @app-state))]]
       [ui/col
        [ui/space {:split (r/as-element [ui/divider {:type "vertical"}])}
         [ui/badge (:page-status @app-state)]
         [ui/button {:loading (:loading? @app-state)
                     :disabled (not (:auto-reload? @app-state))
                     :size "small"
                     :icon (r/as-element [ui/reload-outlined])
                     :onClick (fn [] (update-data!))}
          "обновить сейчас"]]]]
      [ui/row {:style {:margin-top "24px" :margin-bottom "48px"}}
       [ui/col {:span 24}
        [ui/space
         [ui/typography-text "Обновлять данные каждые 10 мин:"]
         [ui/switch {:size "small"
                     :defaultChecked true
                     :checkedChildren "вкл"
                     :unCheckedChildren "выкл"
                     :disabled (:loading? @app-state)
                     :onChange (fn [checked]
                                 (swap! app-state assoc :auto-reload? checked))
                     :onClick (fn [_ _]
                                (if (:auto-reload? @app-state)
                                  (do (update-data!)
                                      (start-job!))
                                  (stop-job!)))}]
         [ui/space
          [ui/typography-text "или показать данные за сутки:"]
          [ui/date-picker {:size "small"
                           :format "DD.MM.YYYY"
                           :defaultValue #(js/moment)
                           :value (:date @app-state)
                           :disabled (or (:auto-reload? @app-state)
                                         (:loading? @app-state))
                           :allowClear false
                           :onChange (fn [date date-str]
                                       (update-data! date date-str))}]]]]]
      [ui/row {:gutter [16 24]}
       (doall
        (map (fn [[[_ description tag] v]]
               ^{:key (js/Math.random)}
               [ui/col {:xs 24 :sm 12 :md 8 :lg 8 :xl 8 :xxl 4}
                [ui/card {:size "small"
                          :title description
                          :headStyle {:background "#fafafa"}}
                 (if tag
                   [:<>
                    [ui/card {:type "inner"
                              :size "small"
                              :title "Сменно-суточный итог"
                              :style {:margin-top "12px"}
                              :headStyle {:background "transparent"}}
                     [ui/space {:direction "vertical"}
                      (map (fn [i]
                             (let [shift (inc i)
                                   hour-weights (map :hour-weight
                                                     (filter #(= shift (:shift %)) v))
                                   entries-count (count hour-weights)]
                               ^{:key (js/Math.random)}
                               [ui/space
                                [ui/typography-text {:strong true}
                                 (str shift " смена: ")]
                                [ui/typography-text {:strong true}
                                 (str (format-number (reduce + 0 hour-weights) "ru-RU") " т")]
                                [ui/tooltip {:title (if (zero? entries-count)
                                                      "Нет данных для расчета"
                                                      (str "Сумма за "
                                                           entries-count " ч смены"))}
                                 [ui/progress {:size "small"
                                               :steps 8
                                               :percent (* 100 (/ entries-count 8))}]]]))
                           (range 3))
                      [ui/typography-text {:strong true}
                       (str "Сутки: "
                            (format-number (reduce + 0 (map :hour-weight v)) "ru-RU")
                            " т")]]]
                    [ui/card {:type "inner"
                              :size "small"
                              :title "Расход, т/ч"
                              :style {:margin-top "24px"}
                              :headStyle {:background "transparent"}
                              :extra (r/as-element
                                      [ui/tooltip {:title "Столбчатая диаграмма"}
                                       [ui/typography-link {:onClick #(swap! app-state
                                                                             assoc
                                                                             :modal-title description
                                                                             :modal? true
                                                                             :tag tag)}
                                        [ui/bar-chart-outlined]]])}
                     [ui/timeline {:mode "left"
                                   :style {:margin-top "12px"}}
                      (doall
                       (map (fn [{:keys [date-end hour-weight]}]
                              ^{:key (js/Math.random)}
                              [ui/timeline-item {:label (last (s/split date-end #" "))}
                               [ui/typography-text
                                (str (format-number hour-weight "ru-RU") " т")]])
                            v))]]]
                   [ui/alert {:showIcon true
                              :type "error"
                              :message "Данные не найдены"}])]])
             (sort
              (group-by
               (juxt :ordering :description :tag)
               (:data @app-state)))))]]]]])

(defn mount-root
  []
  (rd/render
   [view]
   (.getElementById js/document "app")))

(defn dev-setup
  []
  (when DEBUG (enable-console-print!)))

(defn ^:export init
  []
  (dev-setup)
  (update-data!)
  (start-job!)
  (mount-root))

(defn ^:after-load on-reload
  []
  (mount-root))
