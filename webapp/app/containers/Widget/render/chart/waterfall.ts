

import { IChartProps } from '../../components/Chart'
import {
  decodeMetricName,
  getChartTooltipLabel,
  getAggregatorLocale
} from '../../components/util'
import {
  getDimetionAxisOption,
  getMetricAxisOption,
  getLabelOption,
  getLegendOption,
  getGridPositions,
  makeGrouped,
  distinctXaxis
} from './util'
import { EChartOption } from 'echarts'
const defaultTheme = require('../../../../assets/json/echartsThemes/default.project.json')
const defaultThemeColors = defaultTheme.theme.color

export default function (chartProps: IChartProps) {
  const {
    data,
    cols,
    metrics,
    chartStyles
  } = chartProps

  const {
    spec,
    label,
    xAxis,
    yAxis,
    splitLine
  } = chartStyles

  const {
    showVerticalLine,
    verticalLineColor,
    verticalLineSize,
    verticalLineStyle,
    showHorizontalLine,
    horizontalLineColor,
    horizontalLineSize,
    horizontalLineStyle
  } = splitLine

  const labelOption = {
    label: getLabelOption('bar', label)
  }

  const xAxisData = data.map((d) => d[cols[0].name] || '')
  let sourceData = []

  const series = []

  metrics.forEach((m) => {
    const metricName = `${m.agg}(${decodeMetricName(m.name)})`
    sourceData = data.map((d) => d[metricName])
    const baseData = []
    const seriesBaseData = [...data]
    const ascendOrder = []
    const discendOrder = []
    sourceData.forEach((a, index) => {
      a = parseFloat(a)
      if (index > 0) {
        const result = a - parseFloat(sourceData[index - 1])
        if (result >= 0) {
          ascendOrder.push(result)
          discendOrder.push('-')
          baseData.push(parseFloat(sourceData[index - 1]))
        } else {
          ascendOrder.push('-')
          discendOrder.push(Math.abs(result))
          baseData.push(parseFloat(sourceData[index - 1]) - Math.abs(result))
        }
        return result
      } else {
        ascendOrder.push(a)
        discendOrder.push('-')
        baseData.push(0)
        return a
      }
    })
    const totalAscend = ascendOrder.reduce((sum, val) => typeof val === 'number' ? sum + val : sum + 0, 0)
    const totalDiscendOrder = discendOrder.reduce((sum, val) => typeof val === 'number' ? sum + val : sum + 0, 0)
    const difference = totalAscend - totalDiscendOrder
    xAxisData.push('累计')
    baseData.push('-')
    if (difference > 0) {
      ascendOrder.push(difference)
      discendOrder.push('-')
    } else {
      discendOrder.push(Math.abs(difference))
      ascendOrder.push('-')
    }
    const baseDataObj = {
      name: `[${getAggregatorLocale(m.agg)}] ${decodeMetricName(m.name)}`,
      type: 'bar',
      sampling: 'average',
      stack: 'stack',
      data: baseData,
      itemStyle: {
        normal: {
          barBorderColor: 'rgba(0,0,0,0)',
          color: 'rgba(0,0,0,0)'
          // opacity: interactIndex === undefined ? 1 : 0.25
        },
        emphasis: {
          barBorderColor: 'rgba(0,0,0,0)',
          color: 'rgba(0,0,0,0)'
        }
      }
    }

    const ascendOrderObj = {
      name: '升',
      type: 'bar',
      sampling: 'average',
      stack: 'stack',
      data: ascendOrder,
      itemStyle: {
        // normal: {
        //   opacity: interactIndex === undefined ? 1 : 0.25
        // }
      },
      ...labelOption
    }

    const discendOrderObj = {
      name: '降',
      type: 'bar',
      sampling: 'average',
      stack: 'stack',
      data: discendOrder,
      itemStyle: {
        // normal: {
        //   opacity: interactIndex === undefined ? 1 : 0.25
        // }
      },
      ...labelOption
    }
    series.push(baseDataObj)
    series.push(ascendOrderObj)
    series.push(discendOrderObj)
  })

  const seriesNames = series.map((s) => s.name)

  const xAxisSplitLineConfig = {
    showLine: showVerticalLine,
    lineColor: verticalLineColor,
    lineSize: verticalLineSize,
    lineStyle: verticalLineStyle
  }

  const yAxisSplitLineConfig = {
    showLine: showHorizontalLine,
    lineColor: horizontalLineColor,
    lineSize: horizontalLineSize,
    lineStyle: horizontalLineStyle
  }

  const tooltip: EChartOption.Tooltip = {
    trigger: 'axis',
    formatter (param: any[]) {
      const text = param.map((pa, index) => {
        const data = !index ? parseFloat(sourceData[pa.dataIndex]) : pa.data
        return `${pa.seriesName}: ${data}`
      })
      const xAxis = param[0]['axisValue']
      if (xAxis === '累计') {
        return ''
      } else {
        text.unshift(xAxis)
        return text.join('<br/>')
      }
    }
  }

  return {
    xAxis: getDimetionAxisOption(xAxis, xAxisSplitLineConfig, xAxisData),
    yAxis: getMetricAxisOption(yAxis, yAxisSplitLineConfig, metrics.map((m) => decodeMetricName(m.name)).join(` / `)),
    series,
    tooltip,
    grid: getGridPositions({ showLegend: false }, seriesNames, '', false, yAxis, xAxis, xAxisData)
  }
}
