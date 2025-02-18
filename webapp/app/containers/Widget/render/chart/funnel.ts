

import { IChartProps } from '../../components/Chart'
import {
  decodeMetricName,
  getChartTooltipLabel,
  getTextWidth
} from '../../components/util'
import {
  getLegendOption,
  getLabelOption
} from './util'

export default function (chartProps: IChartProps, drillOptions?: any) {
  const {
    width,
    height,
    data,
    cols,
    metrics,
    chartStyles,
    color,
    tip
  } = chartProps

  const {
    label,
    legend,
    spec,
    toolbox
  } = chartStyles

  const {
    legendPosition,
    fontSize
  } = legend

  const {
    alignmentMode,
    gapNumber,
    sortMode
  } = spec

  const labelOption = {
    label: getLabelOption('funnel', label)
  }

  const { selectedItems } = drillOptions

  let seriesObj = {}
  const seriesArr = []
  let legendData = []
  metrics.forEach((m) => {
    const decodedMetricName = decodeMetricName(m.name)
    if (cols.length || color.items.length) {
      const groupColumns = color.items.map((c) => c.name).concat(cols.map((c) => c.name))
      .reduce((distinctColumns, col) => {
        if (!distinctColumns.includes(col)) {
          distinctColumns.push(col)
        }
        return distinctColumns
      }, [])
      const grouped = data.reduce((obj, val) => {
        const groupingKey = groupColumns
          .reduce((keyArr, col) => keyArr.concat(val[col]), [])
          .join(String.fromCharCode(0))
        if (!obj[groupingKey]) {
          obj[groupingKey] = []
        }
        obj[groupingKey].push(val)
        return obj
      }, {})

      const seriesData = []
      Object.entries(grouped).forEach(([key, value]) => {
        const legendStr = key.replace(String.fromCharCode(0), ' ')
        legendData.push(legendStr)
        value.forEach((v) => {
          const obj = {
            name: legendStr,
            value: v[`${m.agg}(${decodedMetricName})`]
          }
          seriesData.push(obj)
        })
      })

      const maxValue = Math.max(...data.map((s) => s[`${m.agg}(${decodedMetricName})`]))
      const minValue = Math.min(...data.map((s) => s[`${m.agg}(${decodedMetricName})`]))

      const numValueArr = data.map((d) => d[`${m.agg}(${decodedMetricName})`] >= 0)
      const minSizePer = minValue / maxValue * 100
      const minSizeValue = numValueArr.indexOf(false) === -1 ? `${minSizePer}%` : '0%'

      const funnelLeft = 56 + Math.max(...legendData.map((s) => getTextWidth(s, '', `${fontSize}px`)))
      const leftValue = legendPosition === 'left'
      ? width * 0.15 + funnelLeft
      : width * 0.15
      const topValue = legendPosition === 'top'
        ? height * 0.12 + 32
        : height * 0.12

      const heightValue = legendPosition === 'left' || legendPosition === 'right'
        ? height - height * 0.12 * 2
        : height - 32 - height * 0.12 * 2
      const widthValue = legendPosition === 'left' || legendPosition === 'right'
        ? width - funnelLeft - width * 0.15 * 2
        : width - width * 0.15 * 2

      let colorArr = []
      if (color.items.length) {
        const colorvaluesObj = color.items[0].config.values
        for (const keys in colorvaluesObj) {
          if (colorvaluesObj.hasOwnProperty(keys)) {
            colorArr.push(colorvaluesObj[keys])
          }
        }
      } else {
        colorArr = ['#509af2']
      }

      seriesObj = {
        name: '',
        type: 'funnel',
        min: minValue,
        max: maxValue,
        minSize: minSizeValue,
        maxSize: '100%',
        sort: sortMode,
        funnelAlign: alignmentMode,
        gap: gapNumber || 0,
        left: leftValue,
        top: topValue,
        width: widthValue,
        height: heightValue,
        color: colorArr,
      //  data: seriesData,
        data: seriesData.map((data, index) => {
          const itemStyleObj = selectedItems && selectedItems.length && selectedItems.some((item) => item === index) ? {itemStyle: {
            normal: {
              opacity: 1
            }
          }} : {}
          return {
            ...data,
            ...itemStyleObj
          }
        }),
        itemStyle: {
          emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
          },
          normal: {
            opacity: selectedItems && selectedItems.length > 0 ? 0.25 : 1
          }
        },
        ...labelOption
      }
    } else {
      legendData = []
      seriesObj = {
        name: '',
        type: 'funnel',
        sort: sortMode,
        funnelAlign: alignmentMode,
        gap: gapNumber || 0,
        left: width * 0.15,
        top: height * 0.12,
        width: width - width * 0.15 * 2,
        height: height - height * 0.12 * 2,
        data: data.map((d, index) => {
          const itemStyleObj = selectedItems && selectedItems.length && selectedItems.some((item) => item === index) ? {itemStyle: {
            normal: {
              opacity: 1
            }
          }} : {}
          return {
            name: decodedMetricName,
            value: d[`${m.agg}(${decodedMetricName})`],
            ...itemStyleObj
          }
        }),
        itemStyle: {
          emphasis: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
          },
          normal: {
            opacity: selectedItems && selectedItems.length > 0 ? 0.25 : 1
          }
        },
        ...labelOption
      }
    }
    seriesArr.push(seriesObj)
  })

  return {
    tooltip: {
      formatter: getChartTooltipLabel('funnel', data, { cols, metrics, color, tip })
    },
    legend: getLegendOption(legend, legendData),
    series: seriesArr
  }
}
