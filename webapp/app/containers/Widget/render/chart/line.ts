

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
const defaultTheme = require('../../../../assets/json/echartsThemes/default.project.json')
const defaultThemeColors = defaultTheme.theme.color

export default function (chartProps: IChartProps, drillOptions?: any) {
  const {
    data,
    cols,
    metrics,
    chartStyles,
    color,
    tip
  } = chartProps

  const {
    spec,
    xAxis,
    yAxis,
    splitLine,
    label,
    legend
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

  const {
    smooth,
    step
  } = spec

  const { selectedItems } = drillOptions

  const labelOption = {
    label: getLabelOption('line', label)
  }

  const xAxisColumnName = cols[0].name
  let xAxisData = data.map((d) => d[xAxisColumnName] || '')
  let grouped = {}
  if (color.items.length) {
    xAxisData = distinctXaxis(data, xAxisColumnName)
    grouped = makeGrouped(data, color.items.map((c) => c.name), xAxisColumnName, metrics, xAxisData)
  }

  const series = []
  const seriesData = []

  metrics.forEach((m, i) => {
    const decodedMetricName = decodeMetricName(m.name)
    const localeMetricName = `[${getAggregatorLocale(m.agg)}] ${decodedMetricName}`
    if (color.items.length) {
      Object
        .entries(grouped)
        .forEach(([k, v]: [string, any[]]) => {
          const serieObj = {
            name: `${k} ${localeMetricName}`,
            type: 'line',
            sampling: 'average',
            data: v.map((g, index) => {
              const itemStyleObj = selectedItems && selectedItems.length && selectedItems.some((item) => item === index) ? {itemStyle: {
                normal: {
                  opacity: 1,
                  borderWidth: 6
                }
              }} : {}
              // if (index === interactIndex) {
              //   return {
              //     value: g[m],
              //     itemStyle: {
              //       normal: {
              //         opacity: 1
              //       }
              //     }
              //   }
              // } else {
              // return g[`${m.agg}(${decodedMetricName})`]
              return {
                value: g[`${m.agg}(${decodedMetricName})`],
                ...itemStyleObj
              }
              // }
            }),
            itemStyle: {
              normal: {
                // opacity: interactIndex === undefined ? 1 : 0.25
                color: color.items[0].config.values[k],
                opacity: selectedItems && selectedItems.length > 0 ? 0.7 : 1
              }
            },
            smooth,
            step,
            ...labelOption
          }
          series.push(serieObj)
          seriesData.push(grouped[k])
        })
    } else {
      const serieObj = {
        name: decodedMetricName,
        type: 'line',
        sampling: 'average',
        data: data.map((g, index) => {
          const itemStyleObj = selectedItems && selectedItems.length && selectedItems.some((item) => item === index) ? {itemStyle: {
            normal: {
              opacity: 1,
              borderWidth: 8
            }
          }} : {}
          // if (index === interactIndex) {
          //   return {
          //     value: d[m],
          //     lineStyle: {
          //       normal: {
          //         opacity: 1
          //       }
          //     },
          //     itemStyle: {
          //       normal: {
          //         opacity: 1
          //       }
          //     }
          //   }
          // } else {
          return {
            value: g[`${m.agg}(${decodedMetricName})`],
            ...itemStyleObj
          }
          // }
        }),
        // lineStyle: {
        //   normal: {
        //     opacity: interactIndex === undefined ? 1 : 0.25
        //   }
        // },
        itemStyle: {
          normal: {
            // opacity: interactIndex === undefined ? 1 : 0.25
            color: color.value[m.name] || defaultThemeColors[i],
            opacity: selectedItems && selectedItems.length > 0 ? 0.7 : 1
          }
        },
        smooth,
        step,
        ...labelOption
      }
      series.push(serieObj)
      seriesData.push([...data])
    }
  })

  const seriesNames = series.map((s) => s.name)

  // dataZoomOptions = dataZoomThreshold > 0 && dataZoomThreshold < dataSource.length && {
  //   dataZoom: [{
  //     type: 'inside',
  //     start: Math.round((1 - dataZoomThreshold / dataSource.length) * 100),
  //     end: 100
  //   }, {
  //     start: Math.round((1 - dataZoomThreshold / dataSource.length) * 100),
  //     end: 100,
  //     handleIcon: 'M10.7,11.9v-1.3H9.3v1.3c-4.9,0.3-8.8,4.4-8.8,9.4c0,5,3.9,9.1,8.8,9.4v1.3h1.3v-1.3c4.9-0.3,8.8-4.4,8.8-9.4C19.5,16.3,15.6,12.2,10.7,11.9z M13.3,24.4H6.7V23h6.6V24.4z M13.3,19.6H6.7v-1.4h6.6V19.6z',
  //     handleSize: '80%',
  //     handleStyle: {
  //       color: '#fff',
  //       shadowBlur: 3,
  //       shadowColor: 'rgba(0, 0, 0, 0.6)',
  //       shadowOffsetX: 2,
  //       shadowOffsetY: 2
  //     }
  //   }]
  // }

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

  return {
    xAxis: getDimetionAxisOption(xAxis, xAxisSplitLineConfig, xAxisData),
    yAxis: getMetricAxisOption(yAxis, yAxisSplitLineConfig, metrics.map((m) => decodeMetricName(m.name)).join(` / `)),
    series,
    tooltip: {
      formatter: getChartTooltipLabel('line', seriesData, { cols, metrics, color, tip })
    },
    legend: getLegendOption(legend, seriesNames),
    grid: getGridPositions(legend, seriesNames, '', false, yAxis, xAxis, xAxisData)
  }
}
