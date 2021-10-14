import React from 'react';
import ReactDOM from 'react-dom';
import ReactDomServer from 'react-dom/server';

window.React = React;
window.ReactDOM = ReactDOM;
window.ReactDOMServer = ReactDomServer;

import moment from 'moment';
import 'moment/locale/ru';

import locale from 'antd/lib/locale/ru_RU';

import {
  ConfigProvider,
  Layout,
  DatePicker,
  Button,
  Row,
  Col,
  Space,
  Typography,
  Divider,
  Card,
  Timeline,
  Switch,
  Badge,
  Tooltip,
  Modal,
  Alert,
  Progress,
  BackTop
} from 'antd';

import {
  BarChartOutlined,
  ReloadOutlined
} from '@ant-design/icons';

window.moment = moment;

window.antd = {
  // Icons
  BarChartOutlined,
  ReloadOutlined,
  // Props
  locale,
  // Components
  ConfigProvider,
  Layout,
  DatePicker,
  Button,
  Row,
  Col,
  Space,
  Typography,
  Divider,
  Card,
  Timeline,
  Switch,
  Badge,
  Tooltip,
  Modal,
  Alert,
  Progress,
  BackTop
};

import Chart from 'chart.js/auto';

window.chart = Chart;