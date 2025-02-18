

import '@babel/polyfill'

import * as React from 'react'
import * as ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { applyRouterMiddleware, Router, hashHistory } from 'react-router'
import { syncHistoryWithStore } from 'react-router-redux'
import { useScroll } from 'react-router-scroll'
import { hot } from 'react-hot-loader'

import App from './containers/App'
import { makeSelectLocationState } from './containers/App/selectors'
import { LocaleProvider } from 'antd'
import zh_CN from 'antd/lib/locale-provider/zh_CN'
import LanguageProvider from './containers/LanguageProvider'
import { translationMessages } from './i18n'
import moment from 'moment'
import 'moment/locale/zh-cn'
moment.locale('zh-cn')

import '!file-loader?name=[name].[ext]!./favicon.ico'
import 'file-loader?name=[name].[ext]!./.htaccess'
import '../libs/react-grid-layout/css/styles.css'
import '../libs/react-resizable/css/styles.css'
import 'bootstrap-datepicker/dist/css/bootstrap-datepicker3.standalone.min.css'
import 'react-quill/dist/quill.snow.css'
import './assets/fonts/iconfont.css'
import './assets/override/antd.css'
import './assets/override/react-grid.css'
import './assets/override/datepicker.css'
import './assets/override/react-color.css'
import './assets/less/style.less'

import * as echarts from 'echarts/lib/echarts'
import 'zrender/lib/svg/svg'
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/line'
import 'echarts/lib/chart/scatter'
import 'echarts/lib/chart/pie'
import 'echarts/lib/chart/sankey'
import 'echarts/lib/chart/funnel'
import 'echarts/lib/chart/map'
import 'echarts/lib/chart/lines'
import 'echarts/lib/chart/effectScatter'
import 'echarts/lib/chart/treemap'
import 'echarts/lib/chart/heatmap'
import 'echarts/lib/chart/boxplot'
import 'echarts/lib/chart/graph'
import 'echarts/lib/chart/gauge'
import 'echarts/lib/chart/radar'
import 'echarts/lib/chart/parallel'
import 'echarts/lib/chart/pictorialBar'
import 'echarts-wordcloud'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/legendScroll'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/toolbox'
import 'echarts/lib/component/dataZoom'
import 'echarts/lib/component/visualMap'
import 'echarts/lib/component/geo'
import 'echarts/lib/component/brush'
import './assets/js/china.js'

import { DEFAULT_ECHARTS_THEME } from './globalConstants'
echarts.registerTheme('default', DEFAULT_ECHARTS_THEME)

import configureStore from './store'
import createRoutes from './routes'

const initialState = {}
const store = configureStore(initialState, hashHistory)
const MOUNT_NODE = document.getElementById('app')
const history = syncHistoryWithStore(hashHistory, store, {
  selectLocationState: makeSelectLocationState()
})

const rootRoute = {
  path: '/',
  component: hot(module)(App),
  childRoutes: createRoutes(store),
  indexRoute: {
    onEnter: (_, replace) => {
    //  replace('/report')
      replace('/projects')
    }
  }
}

const render = (messages) => {
  ReactDOM.render(
    <Provider store={store}>
      <LanguageProvider messages={messages}>
        <LocaleProvider locale={zh_CN}>
          <Router
            history={history}
            routes={rootRoute}
            render={applyRouterMiddleware(useScroll())}
          />
        </LocaleProvider>
      </LanguageProvider>
    </Provider>,
    MOUNT_NODE
  )
}
// declare const module: any
if (module.hot) {
  module.hot.accept(['./i18n', 'containers/App'], () => {
    ReactDOM.unmountComponentAtNode(MOUNT_NODE)
    render(translationMessages)
  })
}

interface IWindow extends Window {
  Intl: any
  __REACT_DEVTOOLS_GLOBAL_HOOK__: any
}
declare const window: IWindow

if (!window.Intl) {
  (new Promise((resolve) => {
    resolve(import('intl'))
  }))
    .then(() => Promise.all([
      import('intl/locale-data/jsonp/en.js')
    ]))
    .then(() => render(translationMessages))
    .catch((err) => {
      throw err
    })
} else {
  render(translationMessages)
}

// Install ServiceWorker and AppCache in the end since
// it's not most important operation and if main code fails,
// we do not want it installed
if (process.env.NODE_ENV === 'production') {
  // disable react developer tools in production
  if (window.__REACT_DEVTOOLS_GLOBAL_HOOK__) {
    window.__REACT_DEVTOOLS_GLOBAL_HOOK__.inject = () => void 0
  }
}

// if (process.env.NODE_ENV !== 'production') {
//   const { whyDidYouUpdate } = require('why-did-you-update')
//   whyDidYouUpdate(React)
// }

