

import React from 'react'
import Helmet from 'react-helmet'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
// import html2canvas from 'html2canvas'
import { compose } from 'redux'
import injectReducer from 'utils/injectReducer'
import injectSaga from 'utils/injectSaga'
import reducer from './reducer'
import saga from './sagas'

import Container from '../../../app/components/Container'
import { getMappingLinkage, processLinkage, removeLinkage } from 'components/Linkages'
import DashboardItem from '../../../app/containers/Dashboard/components/DashboardItem'
import FullScreenPanel from '../../../app/containers/Dashboard/components/fullScreenPanel/FullScreenPanel'
import { Responsive, WidthProvider } from '../../../libs/react-grid-layout'
import { ChartTypes } from '../../../app/containers/Widget/config/chart/ChartTypes'
import { IMapItemControlRequestParams, IMapControlOptions } from '../../../app/components/Filters'
import GlobalControlPanel from '../../../app/components/Filters/FilterPanel'

import { RenderType, IWidgetConfig, IWidgetProps } from '../../../app/containers/Widget/components/Widget'
import { ViewActions } from '../../../app/containers/View/actions'
const { loadViewsDetail } = ViewActions
import { Row, Col, message } from 'antd'

import {
  getDashboard,
  getWidget,
  getResultset,
  setIndividualDashboard,
  loadWidgetCsv,
  loadSelectOptions,
  resizeAllDashboardItem,
  drillDashboardItem,
  deleteDrillHistory,
  setSelectOptions,
  selectDashboardItemChart,
  globalControlChange
} from './actions'
import {
  makeSelectDashboard,
  makeSelectTitle,
  makeSelectConfig,
  makeSelectDashboardSelectOptions,
  makeSelectWidgets,
  makeSelectItems,
  makeSelectItemsInfo,
  makeSelectLinkages
} from './selectors'
import { decodeMetricName, getTable } from '../../../app/containers/Widget/components/util'
import {
  GRID_COLS,
  GRID_ROW_HEIGHT,
  GRID_ITEM_MARGIN,
  GRID_BREAKPOINTS,
  DEFAULT_TABLE_PAGE_SIZE,
  DEFAULT_TABLE_PAGE
} from '../../../app/globalConstants'

const styles = require('../../../app/containers/Dashboard/Dashboard.less')

import Login from '../../components/Login/index'
import { IQueryConditions, IDataRequestParams, QueryVariable } from '../../../app/containers/Dashboard/Grid'

const ResponsiveReactGridLayout = WidthProvider(Responsive)

interface IDashboardProps {
  dashboard: any
  title: string
  config: string
  currentItems: any[],
  currentItemsInfo: {
    [key: string]: {
      datasource: {
        pageNo: number
        pageSize: number
        resultList: any[]
        totalCount: number
      }
      selectedItems?: number[]
      loading: boolean
      queryConditions: IQueryConditions
      downloadCsvLoading: boolean
      renderType: RenderType,
      controlSelectOptions: IMapControlOptions
    }
  },
  widgets: any[],
  dashboardSelectOptions: any,
  linkages: any[]
  onLoadDashboard: (shareInfo: any, error: (err) => void) => void,
  onLoadWidget: (aesStr: string, success?: (widget) => void, error?: (err) => void) => void,
  onLoadResultset: (
    renderType: RenderType,
    dashboardItemId: number,
    dataToken: string,
    requestParams: IDataRequestParams
  ) => void,
  onSetIndividualDashboard: (id, shareInfo) => void,
  onLoadWidgetCsv: (
    itemId: number,
    requestParams: IDataRequestParams,
    dataToken: string
  ) => void,
  onLoadSelectOptions: (controlKey, dataToken, paramsOrOptions, itemId?: number) => void
  onSetSelectOptions: (controlKey: string, options: any[], itemId?: number) => void
  onResizeAllDashboardItem: () => void
  onDrillDashboardItem: (itemId: number, drillHistory: any) => void
  onDeleteDrillHistory: (itemId: number, index: number) => void
  onSelectDashboardItemChart: (itemId: number, renderType: string, selectedItems: number[]) => void
  onGlobalControlChange: (controlRequestParamsByItem: IMapItemControlRequestParams) => void
}

interface IDashboardStates {
  type: string,
  shareInfo: string,
  modalLoading: boolean,
  interactingStatus: { [itemId: number]: boolean }
  allowFullScreen: boolean,
  currentDataInFullScreen: any,
  showLogin: boolean,
  phantomRenderSign: boolean
  controlTokenMapping: {
    [key: string]: string
  }
}

export class Share extends React.Component<IDashboardProps, IDashboardStates> {
  constructor (props) {
    super(props)
    this.state = {
      type: '',
      shareInfo: '',

      modalLoading: false,
      interactingStatus: {},
      allowFullScreen: false,
      currentDataInFullScreen: {},
      showLogin: false,

      phantomRenderSign: false,

      controlTokenMapping: {}
    }
  }

  private interactCallbacks: object = {}
  private interactingLinkagers: object = {}
  private interactGlobalFilters: object = {}
  private resizeSign: number = 0

  /**
   * object
   * {
   *  type: this.state.type,
   *  shareInfo: this.state.shareInfo
   * }
   * @param qs
   */
  private loadShareContent = (qs) => {
    const {
      onLoadDashboard,
      onLoadWidget,
      onSetIndividualDashboard
    } = this.props

    if (qs.type === 'dashboard') {
      onLoadDashboard(qs.shareInfo, (err) => {
        if (err.response.status === 403) {
          this.setState({
            showLogin: true
          })
        }
      })
    } else {
      onLoadWidget(qs.shareInfo, (w) => {
        onSetIndividualDashboard(w.id, qs.shareInfo)
      }, (err) => {
        if (err.response.status === 403) {
          this.setState({
            showLogin: true
          })
        }
      })
    }
  }
  public componentWillMount () {
    const qs = this.getQs(location.href.substr(location.href.indexOf('?') + 1))
    this.setState({
      type: qs.type,
      shareInfo: qs.shareInfo
    })
    this.loadShareContent(qs)
  }

  public componentDidMount () {
    window.addEventListener('resize', this.onWindowResize, false)
  }

  public componentWillReceiveProps (nextProps: IDashboardProps) {
    const { currentItems, currentItemsInfo, dashboard, widgets } = nextProps
    if (currentItemsInfo) {
      if (Object.values(currentItemsInfo).filter((info) => !!info.datasource.resultList.length).length === currentItems.length) {
        // FIXME
        setTimeout(() => {
          this.setState({
            phantomRenderSign: true
          })

          // html2canvas(document.body, { useCORS: true }).then((canvas) => {
          //   const b64 = canvas.toDataURL("image/jpeg")
          //   const img = document.createElement('img')
          //   img.src = b64
          //   document.body.appendChild(img)
          // })

        }, 5000)
      }
    }
    // if (dashboard && !this.props.dashboard) {
    //   const config = JSON.parse(dashboard.config || '{}')
    //   const globalControls = config.filters || []
    //   const controlTokenMapping = globalControls.map((control) => {
    //   })
    // }
  }

  public componentWillUnmount () {
    window.removeEventListener('resize', this.onWindowResize, false)
  }

  private getQs = (qs) => {
    const qsArr = qs.split('&')
    return qsArr.reduce((acc, str) => {
      const arr = str.split('=')
      acc[arr[0]] = arr[1]
      return acc
    }, {})
  }

  private getChartData = (renderType: RenderType, itemId: number, widgetId: number, queryConditions?: Partial<IQueryConditions>) => {
    this.getData(this.props.onLoadResultset, renderType, itemId, widgetId, queryConditions)
  }

  private downloadCsv = (itemId: number, widgetId: number, shareInfo: string) => {
    this.getData(
      (renderType, itemId, dataToken, queryConditions) => {
        this.props.onLoadWidgetCsv(itemId, queryConditions, dataToken)
      },
      'rerender',
      itemId,
      widgetId
    )
  }

  private getData = (
    callback: (
      renderType: RenderType,
      itemId: number,
      dataToken: string,
      requestParams?: IDataRequestParams
    ) => void,
    renderType: RenderType,
    itemId: number,
    widgetId: number,
    queryConditions?: Partial<IQueryConditions>
  ) => {
    const {
      currentItemsInfo,
      widgets
    } = this.props

    const widget = widgets.find((w) => w.id === widgetId)
    const widgetConfig: IWidgetConfig = JSON.parse(widget.config)
    const { cols, rows, metrics, secondaryMetrics, filters, color, label, size, xAxis, tip, orders, cache, expired } = widgetConfig

    const cachedQueryConditions = currentItemsInfo[itemId].queryConditions

    let tempFilters
    let linkageFilters
    let globalFilters
    let variables
    let linkageVariables
    let globalVariables
    let drillStatus
    let pagination
    let nativeQuery

    if (queryConditions) {
      tempFilters = queryConditions.tempFilters !== void 0 ? queryConditions.tempFilters : cachedQueryConditions.tempFilters
      linkageFilters = queryConditions.linkageFilters !== void 0 ? queryConditions.linkageFilters : cachedQueryConditions.linkageFilters
      globalFilters = queryConditions.globalFilters !== void 0 ? queryConditions.globalFilters : cachedQueryConditions.globalFilters
      variables = queryConditions.variables || cachedQueryConditions.variables
      linkageVariables = queryConditions.linkageVariables || cachedQueryConditions.linkageVariables
      globalVariables = queryConditions.globalVariables || cachedQueryConditions.globalVariables
      drillStatus = queryConditions.drillStatus || void 0
      pagination = queryConditions.pagination || cachedQueryConditions.pagination
      nativeQuery = queryConditions.nativeQuery || cachedQueryConditions.nativeQuery
    } else {
      tempFilters = cachedQueryConditions.tempFilters
      linkageFilters = cachedQueryConditions.linkageFilters
      globalFilters = cachedQueryConditions.globalFilters
      variables = cachedQueryConditions.variables
      linkageVariables = cachedQueryConditions.linkageVariables
      globalVariables = cachedQueryConditions.globalVariables
      pagination = cachedQueryConditions.pagination
      nativeQuery = cachedQueryConditions.nativeQuery
    }

    let groups = cols.concat(rows).filter((g) => g.name !== '指标名称').map((g) => g.name)
    let aggregators =  metrics.map((m) => ({
      column: decodeMetricName(m.name),
      func: m.agg
    }))

    if (secondaryMetrics && secondaryMetrics.length) {
      aggregators = aggregators.concat(secondaryMetrics.map((second) => ({
        column: decodeMetricName(second.name),
        func: second.agg
      })))
    }

    if (color) {
      groups = groups.concat(color.items.map((c) => c.name))
    }
    if (label) {
      groups = groups.concat(label.items
        .filter((l) => l.type === 'category')
        .map((l) => l.name))
      aggregators = aggregators.concat(label.items
        .filter((l) => l.type === 'value')
        .map((l) => ({
          column: decodeMetricName(l.name),
          func: l.agg
        })))
    }
    if (size) {
      aggregators = aggregators.concat(size.items
        .map((s) => ({
          column: decodeMetricName(s.name),
          func: s.agg
        })))
    }
    if (xAxis) {
      aggregators = aggregators.concat(xAxis.items
        .map((l) => ({
          column: decodeMetricName(l.name),
          func: l.agg
        })))
    }
    if (tip) {
      aggregators = aggregators.concat(tip.items
        .map((t) => ({
          column: decodeMetricName(t.name),
          func: t.agg
        })))
    }

    callback(
      renderType,
      itemId,
      widget.dataToken,
      {
        groups: drillStatus && drillStatus.groups ? drillStatus.groups : groups,
        aggregators,
        filters: drillStatus && drillStatus.filter ? drillStatus.filter.sqls : filters.map((i) => i.config.sql),
        tempFilters,
        linkageFilters,
        globalFilters,
        variables,
        linkageVariables,
        globalVariables,
        orders,
        cache,
        expired,
        pagination,
        nativeQuery
      }
    )
  }

  private onWindowResize = () => {
    if (this.resizeSign) {
      clearTimeout(this.resizeSign)
    }
    this.resizeSign = window.setTimeout(() => {
      this.props.onResizeAllDashboardItem()
      clearTimeout(this.resizeSign)
      this.resizeSign = 0
    }, 500)
  }

  private visibleFullScreen = (currentChartData) => {
    const {allowFullScreen} = this.state
    if (currentChartData) {
      this.setState({
        currentDataInFullScreen: currentChartData
      })
    }
    this.setState({
      allowFullScreen: !allowFullScreen
    })
  }

  private currentWidgetInFullScreen = (id) => {
    const {currentItems, currentItemsInfo, widgets} = this.props
    const item = currentItems.find((ci) => ci.id === id)
    const widget = widgets.find((w) => w.id === item.widgetId)
    const data = currentItemsInfo[id]
    const loading = currentItemsInfo['loading']
    this.setState({
      currentDataInFullScreen: {
            itemId: id,
            widgetId: widget.id,
            widget,
            data,
            loading,
            onGetChartData: this.getChartData
        }
    })
  }

  private handleLegitimateUser = () => {
    const {type, shareInfo} = this.state
    this.setState({
      showLogin: false
    }, () => {
      this.loadShareContent({type, shareInfo})
    })
  }

  private checkInteract = (itemId: number) => {
    const { linkages } = this.props
    const isInteractiveItem = linkages.some((lts) => {
      const { trigger } = lts
      const triggerId = +trigger[0]
      return triggerId === itemId
    })

    return isInteractiveItem
  }

  private doInteract = (itemId: number, triggerData) => {
    const {
      currentItems,
      linkages
    } = this.props

    const mappingLinkage = getMappingLinkage(itemId, linkages)
    this.interactingLinkagers = processLinkage(itemId, triggerData, mappingLinkage, this.interactingLinkagers)

    Object.keys(mappingLinkage).forEach((linkagerItemId) => {
      const item = currentItems.find((ci) => ci.id === +linkagerItemId)
      const { filters, variables } = this.interactingLinkagers[linkagerItemId]
      this.getChartData('rerender', +linkagerItemId, item.widgetId, {
        linkageFilters: Object.values(filters).reduce<string[]>((arr, f: string[]) => arr.concat(...f), []),
        linkageVariables: Object.values(variables).reduce<QueryVariable>((arr, p: QueryVariable) => arr.concat(...p), [])
      })
    })
    this.setState({
      interactingStatus: {
        ...this.state.interactingStatus,
        [itemId]: true
      }
    })
  }

  private turnOffInteract = (itemId) => {
    const {
      linkages,
      currentItems
    } = this.props

    const refreshItemIds = removeLinkage(itemId, linkages, this.interactingLinkagers)
    refreshItemIds.forEach((linkagerItemId) => {
      const item = currentItems.find((ci) => ci.id === linkagerItemId)
      const { filters, variables } = this.interactingLinkagers[linkagerItemId]
      this.getChartData('rerender', linkagerItemId, item.widgetId, {
        linkageFilters: Object.values(filters).reduce<string[]>((arr, f: string[]) => arr.concat(...f), []),
        linkageVariables: Object.values(variables).reduce<QueryVariable>((arr, p: QueryVariable) => arr.concat(...p), [])
      })
    })
    this.setState({
      interactingStatus: {
        ...this.state.interactingStatus,
        [itemId]: false
      }
    }, () => {
      const item = currentItems.find((ci) => ci.id === itemId)
      this.getChartData('clear', itemId, item.widgetId)
    })
  }

  private getOptions = (controlKey: string, useOptions: boolean, paramsOrOptions, itemId?: number) => {
    if (useOptions) {
      this.props.onSetSelectOptions(controlKey, paramsOrOptions, itemId)
    } else {
      this.props.onLoadSelectOptions(controlKey, this.state.shareInfo, paramsOrOptions, itemId)
    }
  }

  private globalControlChange = (controlRequestParamsByItem: IMapItemControlRequestParams) => {
    this.props.onGlobalControlChange(controlRequestParamsByItem)
  }

  private globalControlSearch = (itemIds: number[]) => {
    const { currentItems, widgets, currentItemsInfo } = this.props
    itemIds.forEach((itemId) => {
      const item = currentItems.find((ci) => ci.id === itemId)
      if (item) {
        const widget = widgets.find((w) => w.id === item.widgetId)
        const pagination = currentItemsInfo[itemId].queryConditions.pagination
        let pageNo = 0
        let pageSize = DEFAULT_TABLE_PAGE_SIZE
        let noAggregators = false
        if (widget.type === getTable().id) {
          try {
            const widgetProps: IWidgetProps = JSON.parse(widget.config)
            if (widgetProps.mode === 'chart') {
              const table = widgetProps.chartStyles.table
              pageNo = DEFAULT_TABLE_PAGE
              pageSize = Number(table.pageSize)
              noAggregators = table.withNoAggregators
            }
          } catch (error) {
            message.error(error)
          }
        }
        this.getChartData('rerender', itemId, item.widgetId, {
          pagination: { pageSize, ...pagination, pageNo },
          nativeQuery: noAggregators
        })
      }
    })
  }

  private globalFilterChange = (queryConditions: IMapItemControlRequestParams) => {
    const { currentItems, currentItemsInfo } = this.props
    Object.entries(queryConditions).forEach(([itemId, condition]) => {
      const item = currentItems.find((ci) => ci.id === +itemId)
      if (item) {
        let pageNo = 0
        const { pagination } = currentItemsInfo[itemId].queryConditions
        if (pagination.pageNo) { pageNo = 1 }
        const { variables: globalVariables, filters: globalFilters } = condition
        this.getChartData('rerender', +itemId, item.widgetId, {
          globalVariables,
          globalFilters,
          pagination: { ...pagination, pageNo }
        })
      }
    })
  }


  private dataDrill = (e) => {
    const {
      widgets,
      currentItemsInfo,
      onDrillDashboardItem
    } = this.props
    const { itemId, groups, widgetId, sourceDataFilter, mode, col, row } = e
    const widget = widgets.find((w) => w.id === widgetId)
    const widgetConfig: IWidgetConfig = JSON.parse(widget.config)
    const { cols, rows, metrics, filters, color, label, size, xAxis, tip, orders, cache, expired } = widgetConfig
    const drillHistory = currentItemsInfo[itemId].queryConditions.drillHistory
    let sql = void 0
    let name = void 0
    let filterSource = void 0
    let widgetConfigGroups = cols.concat(rows).filter((g) => g.name !== '指标名称').map((g) => g.name)
    let aggregators =  metrics.map((m) => ({
      column: decodeMetricName(m.name),
      func: m.agg
    }))

    if (color) {
      widgetConfigGroups = widgetConfigGroups.concat(color.items.map((c) => c.name))
    }
    if (label) {
      widgetConfigGroups = widgetConfigGroups.concat(label.items
        .filter((l) => l.type === 'category')
        .map((l) => l.name))
      aggregators = aggregators.concat(label.items
        .filter((l) => l.type === 'value')
        .map((l) => ({
          column: decodeMetricName(l.name),
          func: l.agg
        })))
    }
    let currentDrillStatus = void 0
    let widgetConfigRows = []
    let widgetConfigCols = []
    const coustomTableSqls = []
    let sqls = widgetConfig.filters.map((i) => i.config.sql)
    if ((!drillHistory) || drillHistory.length === 0) {
      let currentCol = void 0
      if (widgetConfig) {
        const dimetionAxis = widgetConfig.dimetionAxis
        widgetConfigRows = widgetConfig.rows && widgetConfig.rows.length ? widgetConfig.rows : []
        widgetConfigCols = widgetConfig.cols && widgetConfig.cols.length ? widgetConfig.cols : []
        const mode = widgetConfig.mode
        if (mode && mode === 'pivot') {
          if (cols && cols.length !== 0) {
            const cols = widgetConfig.cols
            name = cols[cols.length - 1]['name']
          } else {
            const rows = widgetConfig.rows
            name = rows[rows.length - 1]['name']
          }
        } else if (dimetionAxis === 'col') {
          const cols = widgetConfig.cols
          name = cols[cols.length - 1]['name']
        } else if (dimetionAxis === 'row') {
          const rows = widgetConfig.rows
          name = rows[rows.length - 1]['name']
        } else if (mode === 'chart'  && widgetConfig.selectedChart === ChartTypes.Table) {
          // todo coustomTable
          const coustomTable = sourceDataFilter.reduce((a, b) => {
            a[b['key']] === undefined ? a[b['key']] = [b['value']] : a[b['key']].push(b['value'])
            return a
          }, {})
          for (const attr in coustomTable) {
            if (coustomTable[attr] !== undefined && attr) {
              coustomTableSqls.push(`${attr} in (${coustomTable[attr].map((key) => `'${key}'`).join(',')})`)
            }
          }
          const drillKey = sourceDataFilter[sourceDataFilter.length - 1]['key']
          const newWidgetPropCols = widgetConfigCols.reduce((array, col) => {
            array.push(col)
            if (col.name === drillKey) {
              array.push({name: groups})
            }
            return array
          }, [])
          currentCol = groups && groups.length ? newWidgetPropCols : void 0
        }
      }
      filterSource = sourceDataFilter.map((source) => {
        if (source && source[name]) {
          return source[name]
        }
      })
      if (name && name.length) {
        currentCol = col && col.length ? widgetConfigCols.concat([{name: col}]) : void 0
        sql = `${name} in (${filterSource.map((key) => `'${key}'`).join(',')})`
        sqls.push(sql)
      }
      if (Array.isArray(coustomTableSqls) && coustomTableSqls.length > 0) {
        sqls = sqls.concat(coustomTableSqls)
      }
      const isDrillUp = widgetConfigGroups.some((cg) => cg === groups)
      let currentDrillGroups = void 0
      if (isDrillUp) {
        currentDrillGroups = widgetConfigGroups.filter((cg) => cg !== groups)
      } else {
        if (mode === 'pivot') {
          currentDrillGroups = widgetConfigGroups.concat([groups])
        } else if (mode === 'chart' && widgetConfig.selectedChart === ChartTypes.Table) {
          currentDrillGroups = widgetConfigGroups.concat([groups])
        } else {
          currentDrillGroups = [groups]
        }
      }
      currentDrillStatus = {
        filter: {
          filterSource,
          name,
          sql,
          sqls,
          visualType: 'string'
        },
        type: isDrillUp ? 'up' : 'down',
        col: currentCol,
        row: row && row.length ? widgetConfigRows.concat([{name: row}]) : void 0,
        groups: currentDrillGroups,
        // groups: isDrillUp
        //         ? widgetConfigGroups.filter((cg) => cg !== groups)
        //         : mode === 'pivot' ? widgetConfigGroups.concat([groups])
        //                           : [groups],
        name: groups
      }
    } else {
      const lastDrillHistory = drillHistory[drillHistory.length - 1]
      let currentCol = void 0
      let currentRow = void 0
     // todo
      if (mode === 'chart' && widgetConfig.selectedChart === ChartTypes.Table) {
        const coustomTable = sourceDataFilter.reduce((a, b) => {
          a[b['key']] === undefined ? a[b['key']] = [b['value']] : a[b['key']].push(b['value'])
          return a
        }, {})
        for (const attr in coustomTable) {
          if (coustomTable[attr] !== undefined && attr) {
            coustomTableSqls.push(`${attr} in (${coustomTable[attr].map((key) => `'${key}'`).join(',')})`)
          }
        }
        if (Array.isArray(coustomTableSqls) && coustomTableSqls.length > 0) {
          sqls = sqls.concat(coustomTableSqls)
        }
        if (lastDrillHistory && lastDrillHistory.col && lastDrillHistory.col.length) {
          const drillKey = sourceDataFilter[sourceDataFilter.length - 1]['key']
          const cols = lastDrillHistory.col
          const newWidgetPropCols = cols.reduce((array, col) => {
            array.push(col)
            if (col.name === drillKey) {
              array.push({name: groups})
            }
            return array
          }, [])
          currentCol = groups && groups.length ? newWidgetPropCols : lastDrillHistory.col
        }
      } else {
        name = lastDrillHistory.groups[lastDrillHistory.groups.length - 1]
        filterSource = sourceDataFilter.map((source) => source[name])
        sql = `${name} in (${filterSource.map((key) => `'${key}'`).join(',')})`
        sqls = lastDrillHistory.filter.sqls.concat(sql)
        currentCol = col && col.length ? (lastDrillHistory.col || []).concat({name: col}) : lastDrillHistory.col
        currentRow = row && row.length ? (lastDrillHistory.row || []).concat({name: row}) : lastDrillHistory.row
      }
      const isDrillUp = lastDrillHistory.groups.some((cg) => cg === groups)
      let currentDrillGroups = void 0
      if (isDrillUp) {
        currentDrillGroups = lastDrillHistory.groups.filter((cg) => cg !== groups)
      } else {
        if (mode === 'pivot') {
          currentDrillGroups = lastDrillHistory.groups.concat([groups])
        } else if (mode === 'chart' && widgetConfig.selectedChart === ChartTypes.Table) {
          currentDrillGroups = lastDrillHistory.groups.concat([groups])
        } else {
          currentDrillGroups = [groups]
        }
      }
      currentDrillStatus = {
        filter: {
          filterSource,
          name,
          sql,
          sqls,
          visualType: 'string'
        },
        col: currentCol,
        row: currentRow,
        type: isDrillUp ? 'up' : 'down',
        groups: currentDrillGroups,
        name: groups
      }
    }
    onDrillDashboardItem(itemId, currentDrillStatus)
    this.getChartData('rerender', itemId, widgetId, {
        drillStatus: currentDrillStatus
      })
  }

  private selectDrillHistory = (history, item, itemId, widgetId) => {
    const { currentItemsInfo, onDeleteDrillHistory } = this.props
    if (history) {
      this.getChartData('rerender', itemId, widgetId, {
        drillStatus: history
      })
    } else {
      this.getChartData('rerender', itemId, widgetId)
    }
    onDeleteDrillHistory(itemId, item)
  }

  private selectChartsItems = (itemId, renderType, selectedItems) => {
    const { onSelectDashboardItemChart } = this.props
    onSelectDashboardItemChart(itemId, renderType, selectedItems)
  }

  public render () {
    const {
      dashboard,
      title,
      currentItems,
      currentItemsInfo,
      widgets,
      dashboardSelectOptions
    } = this.props

    const {
      shareInfo,
      showLogin,
      interactingStatus,
      allowFullScreen,
      phantomRenderSign
    } = this.state

    let grids = null
    let fullScreenComponent = null
    let loginPanel = null

    if (currentItems) {
      const itemblocks: React.ReactNode[] = []
      const layouts = {lg: []}

      currentItems.forEach((dashboardItem) => {
        const { id, x, y, width, height, widgetId, polling, frequency } = dashboardItem
        const {
          datasource,
          loading,
          downloadCsvLoading,
          renderType,
          queryConditions,
          controlSelectOptions,
          selectedItems
        } = currentItemsInfo[id]

        const widget = widgets.find((w) => w.id === widgetId)
        let view = { model: widget.model }
        try {
          view = {
            model: JSON.parse(widget.model)
          }
        } catch (error) {
          throw new Error(error)
        }
        const interacting = interactingStatus[id] || false
        const drillHistory = queryConditions.drillHistory
        itemblocks.push((
          <div key={id}>
            <DashboardItem
              itemId={id}
              widget={widget}
              widgets={widgets}
              view={view}
              datasource={datasource}
              loading={loading}
              polling={polling}
              onDrillData={this.dataDrill}
              onSelectDrillHistory={this.selectDrillHistory}
              interacting={interacting}
              drillHistory={drillHistory}
              frequency={frequency}
              shareInfo={widget.dataToken}
              downloadCsvLoading={downloadCsvLoading}
              renderType={renderType}
              controlSelectOptions={controlSelectOptions}
              queryConditions={queryConditions}
              onGetChartData={this.getChartData}
              onDownloadCsv={this.downloadCsv}
              onTurnOffInteract={this.turnOffInteract}
              onCheckTableInteract={this.checkInteract}
              onDoTableInteract={this.doInteract}
              onShowFullScreen={this.visibleFullScreen}
              onGetControlOptions={this.getOptions}
              container="share"
              selectedItems={selectedItems || []}
              onSelectChartsItems={this.selectChartsItems}
            />
          </div>
        ))

        layouts.lg.push({
          x,
          y,
          w: width,
          h: height,
          i: `${id}`
        })
      })

      grids = (
        <ResponsiveReactGridLayout
          className="layout"
          style={{marginTop: '-16px'}}
          rowHeight={GRID_ROW_HEIGHT}
          margin={[GRID_ITEM_MARGIN, GRID_ITEM_MARGIN]}
          breakpoints={GRID_BREAKPOINTS}
          cols={GRID_COLS}
          layouts={layouts}
          measureBeforeMount={false}
          useCSSTransforms={false}
          isDraggable={false}
          isResizable={false}
        >
          {itemblocks}
        </ResponsiveReactGridLayout>
      )

      fullScreenComponent = (
        <FullScreenPanel
          widgets={widgets}
          currentItems={currentItems}
          currentDashboard={{ widgets: currentItems }}
          currentItemsInfo={currentItemsInfo}
          visible={allowFullScreen}
          isVisible={this.visibleFullScreen}
          currentDataInFullScreen={this.state.currentDataInFullScreen}
          onCurrentWidgetInFullScreen={this.currentWidgetInFullScreen}
        />
      )
    } else {
      grids = (
        <div className={styles.shareContentEmpty}>
          <h3>数据加载中……</h3>
        </div>
      )

      fullScreenComponent = ''
    }

    loginPanel = showLogin ? <Login shareInfo={this.state.shareInfo} legitimateUser={this.handleLegitimateUser} /> : ''

    const phantomDOM = phantomRenderSign && (<div id="phantomRenderSign" />)

    return (
      <Container>
        <Helmet title={title} />
        <Container.Title>
          <Row>
            <Col span={24}>
              <h2 className={styles.shareTitle}>{title}</h2>
            </Col>
          </Row>
          <GlobalControlPanel
            currentDashboard={dashboard}
            currentItems={currentItems}
            onGetOptions={this.getOptions}
            mapOptions={dashboardSelectOptions}
            onChange={this.globalControlChange}
            onSearch={this.globalControlSearch}
          />
        </Container.Title>
        {grids}
        <div className={styles.gridBottom} />
        {fullScreenComponent}
        {loginPanel}
        {phantomDOM}
      </Container>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  dashboard: makeSelectDashboard(),
  title: makeSelectTitle(),
  config: makeSelectConfig(),
  widgets: makeSelectWidgets(),
  currentItems: makeSelectItems(),
  currentItemsInfo: makeSelectItemsInfo(),
  dashboardSelectOptions: makeSelectDashboardSelectOptions(),
  linkages: makeSelectLinkages()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLoadViewsDetail: (viewIds, resolve) => dispatch(loadViewsDetail(viewIds, resolve)),
    onLoadDashboard: (token, reject) => dispatch(getDashboard(token, reject)),
    onLoadWidget: (token, resolve, reject) => dispatch(getWidget(token, resolve, reject)),
    onLoadResultset: (renderType, itemid, dataToken, requestParams) => dispatch(getResultset(renderType, itemid, dataToken, requestParams)),
    onSetIndividualDashboard: (widgetId, token) => dispatch(setIndividualDashboard(widgetId, token)),
    onLoadWidgetCsv: (itemId, requestParams, dataToken) => dispatch(loadWidgetCsv(itemId, requestParams, dataToken)),
    onLoadSelectOptions: (controlKey, dataToken, paramsOrOptions, itemId) => dispatch(loadSelectOptions(controlKey, dataToken, paramsOrOptions, itemId)),
    onSetSelectOptions: (controlKey, options, itemId) => dispatch(setSelectOptions(controlKey, options, itemId)),
    onResizeAllDashboardItem: () => dispatch(resizeAllDashboardItem()),
    onDrillDashboardItem: (itemId, drillHistory) => dispatch(drillDashboardItem(itemId, drillHistory)),
    onDeleteDrillHistory: (itemId, index) => dispatch(deleteDrillHistory(itemId, index)),
    onSelectDashboardItemChart: (itemId, renderType, selectedItems) => dispatch(selectDashboardItemChart(itemId, renderType, selectedItems)),
    onGlobalControlChange: (controlRequestParamsByItem) => dispatch(globalControlChange(controlRequestParamsByItem))
  }
}

const withConnect = connect(mapStateToProps, mapDispatchToProps)
const withReducer = injectReducer({ key: 'shareDashboard', reducer })
const withSaga = injectSaga({ key: 'shareDashboard', saga })

export default compose(
  withReducer,
  withSaga,
  withConnect
)(Share)
