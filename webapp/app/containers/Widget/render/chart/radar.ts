

import { IChartProps } from '../../components/Chart'
import {
  decodeMetricName,
  getChartTooltipLabel,
  getSizeValue,
  getSizeRate
} from '../../components/util'
import {
  getMetricAxisOption,
  getLabelOption,
  getLegendOption,
  getSymbolSize
} from './util'

export default function (chartProps: IChartProps) {
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

  const { shape } = spec

  const labelOption = {
    label: getLabelOption('radar', label)
  }

  let dimensions = []
  if (cols.length) {
    dimensions = dimensions.concat(cols)
  }
  if (color.items.length) {
    dimensions = dimensions.concat(color.items.map((c) => c.name))
  }
  const dimension = dimensions[0]

  const metricsNames = metrics.map((m) => decodeMetricName(m.name))
  const legendData = metricsNames
  const indicatorData = {}
  let indicatorMax = -Infinity
  const dimensionData = metricsNames.reduce((acc, name) => ({
    ...acc,
    [name]: {}
  }), {})
  data.forEach((row) => {
    if (!indicatorData[row[dimension.name]]) {
      indicatorData[row[dimension.name]] = -Infinity
    }

    metrics.forEach((m) => {
      const name = decodeMetricName(m.name)
      const cellVal = row[`${m.agg}(${name})`]
      indicatorMax = Math.max(indicatorMax, cellVal)
      if (!dimensionData[name][row[dimension.name]]) {
        dimensionData[name][row[dimension.name]] = 0
      }
      dimensionData[name][row[dimension.name]] += cellVal
    })
  })
  const indicator = Object.keys(indicatorData).map((name: string) => ({
    name,
    max: indicatorMax + Math.round(indicatorMax * 0.1)
  }))
  const seriesData = data.length > 0 ? Object.entries(dimensionData).map(([name, value]) => ({
    name,
    value: Object.values(value)
  })) : []

  const {
    showLabel,
    labelColor,
    labelFontFamily,
    labelFontSize
  } = label

  const radarName = {
    show: showLabel,
    color: labelColor,
    fontFamily: labelFontFamily,
    fontSize: labelFontSize
  }

  return {
    tooltip : {},
    legend: getLegendOption(legend, legendData),
    radar: {
      // type: 'log',
      shape,
      indicator,
      name: radarName
    },
    series: [{
      name: '',
      type: 'radar',
      data: seriesData
    }]
  }
}
