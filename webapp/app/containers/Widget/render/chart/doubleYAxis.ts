

import { IChartProps } from '../../components/Chart'
import {
  decodeMetricName,
  getChartTooltipLabel,
  getTextWidth,
  getAggregatorLocale
} from '../../components/util'
import {
  getLegendOption,
  getGridPositions,
  getDimetionAxisOption
} from './util'

export default function (chartProps: IChartProps, drillOptions) {
  const {
    width,
    height,
    data,
    cols,
    metrics,
    chartStyles
    // color,
    // tip
  } = chartProps

  const {
    legend,
    spec,
    doubleYAxis,
    xAxis,
    splitLine
  } = chartStyles

  const {
    legendPosition,
    fontSize
  } = legend

  const {
    stack,
    smooth,
    step,
    symbol,
    label
  } = spec

  const {
    yAxisLeft,
    yAxisRight,
    yAxisSplitNumber,
    dataZoomThreshold
  } = doubleYAxis

  const {
    labelColor,
    labelFontFamily,
    labelFontSize,
    lineColor,
    lineSize,
    lineStyle,
    showLabel,
    showLine,
    xAxisInterval,
    xAxisRotate
  } = xAxis

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
    label: {
      normal: {
        show: label,
        position: 'top'
      }
    }
  }

  const { selectedItems } = drillOptions
  const { secondaryMetrics } = chartProps

  const seriesData = secondaryMetrics
    ? getAixsMetrics('metrics', metrics, data, stack, labelOption, selectedItems, {key: 'yAxisLeft', type: yAxisLeft})
      .concat(getAixsMetrics('secondaryMetrics', secondaryMetrics, data, stack, labelOption, selectedItems, {key: 'yAxisRight', type: yAxisRight}))
    : getAixsMetrics('metrics', metrics, data, stack, labelOption, selectedItems, {key: 'yAxisLeft', type: yAxisLeft})

  const seriesObj = {
    series: seriesData.map((series) => {
      if (series.type === 'line') {
        return {
          ...series,
          symbol: symbol ? 'emptyCircle' : 'none',
          smooth,
          step
        }
      } else {
        return series
      }
    })
  }

  let legendOption
  let gridOptions
  if (seriesData.length > 1) {
    const seriesNames = seriesData.map((s) => s.name)
    legendOption = {
      legend: getLegendOption(legend, seriesNames)
    }
    gridOptions = {
      grid: getGridPositions(legend, seriesNames, 'doubleYAxis', false)
    }
  }

  let leftMax
  let rightMax

  if (stack) {
    leftMax = metrics.reduce((num, m) => num + Math.max(...data.map((d) => d[`${m.agg}(${decodeMetricName(m.name)})`])), 0)
    rightMax = secondaryMetrics.reduce((num, m) => num + Math.max(...data.map((d) => d[`${m.agg}(${decodeMetricName(m.name)})`])), 0)
  } else {
    leftMax = Math.max(...metrics.map((m) => Math.max(...data.map((d) => d[`${m.agg}(${decodeMetricName(m.name)})`]))))
    rightMax = Math.max(...secondaryMetrics.map((m) => Math.max(...data.map((d) => d[`${m.agg}(${decodeMetricName(m.name)})`]))))
  }

  const leftInterval = getYaxisInterval(leftMax, (yAxisSplitNumber - 1))
  const rightInterval = rightMax > 0 ? getYaxisInterval(rightMax, (yAxisSplitNumber - 1)) : leftInterval

  const inverseOption = xAxis.inverse ? { inverse: true } : null

  const xAxisSplitLineConfig = {
    showLine: showVerticalLine,
    lineColor: verticalLineColor,
    lineSize: verticalLineSize,
    lineStyle: verticalLineStyle
  }
  const xAxisData = showLabel ? data.map((d) => d[cols[0].name]) : []

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {type: 'cross'}
    },
    xAxis: getDimetionAxisOption(xAxis, xAxisSplitLineConfig, xAxisData),
    yAxis: [
      {
        type: 'value',
        key: 'yAxisIndex0',
        min: 0,
        max: rightMax > 0 ? rightInterval * (yAxisSplitNumber - 1) : leftInterval * (yAxisSplitNumber - 1),
        interval: rightInterval,
        position: 'right',
        ...getDoubleYAxis(doubleYAxis)
      },
      {
        type: 'value',
        key: 'yAxisIndex1',
        min: 0,
        max: leftInterval * (yAxisSplitNumber - 1),
        interval: leftInterval,
        position: 'left',
        ...getDoubleYAxis(doubleYAxis)
      }
    ],
    ...seriesObj,
    ...gridOptions,
    ...legendOption
  }

  return option
}

export function getAixsMetrics (type, axisMetrics, data, stack, labelOption, selectedItems, axisPosition?: {key: string, type: string}) {
  const seriesNames = []
  const seriesAxis = []
  axisMetrics.forEach((m) => {
    const decodedMetricName = decodeMetricName(m.name)
    const localeMetricName = `[${getAggregatorLocale(m.agg)}] ${decodedMetricName}`
    seriesNames.push(decodedMetricName)
    const stackOption = stack && axisPosition.type === 'bar' && axisMetrics.length > 1 ? { stack: axisPosition.key } : null
    const itemData = data.map((g, index) => {
      const itemStyle = selectedItems && selectedItems.length && selectedItems.some((item) => item === index) ? {itemStyle: {normal: {opacity: 1, borderWidth: 6}}} : null
      return {
        value: g[`${m.agg}(${decodedMetricName})`],
        ...itemStyle
      }
    })

    seriesAxis.push({
      name: decodedMetricName,
      type: axisPosition && axisPosition.type ? axisPosition.type : type === 'metrics' ? 'line' : 'bar',
      ...stackOption,
      yAxisIndex: type === 'metrics' ? 1 : 0,
      data: itemData,
      ...labelOption,
      itemStyle: {
        normal: {
          opacity: selectedItems && selectedItems.length > 0 ? 0.25 : 1
        }
      }
    })
  })
  return seriesAxis
}

export function getYaxisInterval (max, splitNumber) {
  const roughInterval = parseInt(`${max / splitNumber}`, 10)
  const divisor = Math.pow(10, (`${roughInterval}`.length - 1))
  return (parseInt(`${roughInterval / divisor}`, 10) + 1) * divisor
}

export function getDoubleYAxis (doubleYAxis) {
  const {
    inverse,
    showLine,
    lineStyle,
    lineSize,
    lineColor,
    showLabel,
    labelFontFamily,
    labelFontSize,
    labelColor
  } = doubleYAxis

  return {
    inverse,
    axisLine: {
      show: showLine,
      lineStyle: {
        color: lineColor,
        width: Number(lineSize),
        type: lineStyle
      }
    },
    axisLabel: {
      show: showLabel,
      color: labelColor,
      fontFamily: labelFontFamily,
      fontSize: Number(labelFontSize),
      formatter: '{value}'
    }
  }
}
