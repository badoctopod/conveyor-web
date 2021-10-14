(ns ^:figwheel-hooks conveyor-web.ui
  (:require
   [clojure.string :as s]
   [reagent.core :as r]))

(defn get-module-path
  [module-name]
  (clojure.string/split module-name #"\."))

(defn antd->reagent
  [antd-name]
  (r/adapt-react-class
   (apply goog.object/getValueByKeys
          js/antd (get-module-path antd-name))))

(def bar-chart-outlined (antd->reagent "BarChartOutlined"))
(def reload-outlined (antd->reagent "ReloadOutlined"))
(def config-provider (antd->reagent "ConfigProvider"))
(def date-picker (antd->reagent "DatePicker"))
(def layout (antd->reagent "Layout"))
(def layout-content (antd->reagent "Layout.Content"))
(def button (antd->reagent "Button"))
(def row (antd->reagent "Row"))
(def col (antd->reagent "Col"))
(def space (antd->reagent "Space"))
(def divider (antd->reagent "Divider"))
(def typography-title (antd->reagent "Typography.Title"))
(def typography-link (antd->reagent "Typography.Link"))
(def typography-text (antd->reagent "Typography.Text"))
(def timeline (antd->reagent "Timeline"))
(def timeline-item (antd->reagent "Timeline.Item"))
(def card (antd->reagent "Card"))
(def switch (antd->reagent "Switch"))
(def badge (antd->reagent "Badge"))
(def tooltip (antd->reagent "Tooltip"))
(def modal (antd->reagent "Modal"))
(def alert (antd->reagent "Alert"))
(def progress (antd->reagent "Progress"))
(def backtop (antd->reagent "BackTop"))