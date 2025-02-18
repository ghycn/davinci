

import * as React from 'react'
import { Button, Modal } from 'antd'

import { SQL_NUMBER_TYPES, DEFAULT_SPLITER } from '../../../globalConstants'
import { decodeMetricName, getAggregatorLocale } from '../../Widget/components/util'
import { IFormedViews } from 'containers/View/types'
import { IWidgetConfig } from '../../Widget/components/Widget'
import LinkageConfig from 'components/Linkages/LinkageConfig'

const styles = require('../Dashboard.less')

interface IDashboardLinkageConfigProps {
  currentDashboard: any
  currentItems: any[]
  currentItemsInfo: any
  linkages: any[]
  views: IFormedViews
  widgets: any[]
  visible: boolean
  loading: boolean
  onCancel: () => void
  onSave: (filterItems: any[]) => void
  onGetWidgetInfo: (itemId: number) => void
}

interface IDashboardLinkageConfigStates {
  linkageCascaderSource: any[]
  savingLinkageConfig: boolean
}

export class DashboardLinkageConfig extends React.Component<IDashboardLinkageConfigProps, IDashboardLinkageConfigStates> {

  public constructor (props: IDashboardLinkageConfigProps) {
    super(props)
    this.state = {
      linkageCascaderSource: [],
      savingLinkageConfig: false
    }
  }

  public componentWillReceiveProps (nextProps: IDashboardLinkageConfigProps) {
    const { visible } = nextProps
    if (visible) {
      const linkageCascaderSource = this.getLinkageConfigSource()
      this.setState({ linkageCascaderSource })
    }
  }

  private getLinkageConfigSource = () => {
    const { currentItems, widgets, views, currentItemsInfo } = this.props
    if (!currentItemsInfo) { return [] }

    const linkageConfigSource = []
    Object.keys(currentItemsInfo).forEach((k) => {
      const dashboardItem = currentItems.find((ci) => `${ci.id}` === k)
      const widget = widgets.find((w) => w.id === dashboardItem.widgetId)
      const widgetConfig: IWidgetConfig = JSON.parse(widget.config)
      const { cols, rows, metrics } = widgetConfig

      const view = views[widget.viewId]
      const { model, variable } = view

      // Cascader value 中带有 itemId、字段类型、参数/变量标识 这些信息，用 DEFAULT_SPLITER 分隔
      const columns = [
        ...[...cols, ...rows]
          .filter(({ name }) => model[name])
          .map(({ name }) => {
            return {
              label: name,
              value: [name, model[name].sqlType, 'column'].join(DEFAULT_SPLITER)
            }
          }),
        ...metrics.map(({ name, agg }) => {
          const metricName = decodeMetricName(name)
          return {
            label: `${getAggregatorLocale(agg)} ${metricName}`,
            value: [`${agg}(${metricName})`, SQL_NUMBER_TYPES[SQL_NUMBER_TYPES.length - 1], 'column'].join(DEFAULT_SPLITER)
          }
        })
      ]

      const variables = variable.map(({ name }) => {
        return {
          label: `${name}[变量]`,
          value: [name, null, 'variable'].join(DEFAULT_SPLITER)
        }
      })

      linkageConfigSource.push({
        label: widget.name,
        value: k,
        children: {
          columns,
          variables
        }
      })
    })

    return linkageConfigSource
  }

  private onSavingLinkageConfig = () => {
    this.setState({
      savingLinkageConfig: !this.state.savingLinkageConfig
    })
  }

  public render () {
    const { visible, loading, onSave, onGetWidgetInfo, linkages, onCancel } = this.props
    const { linkageCascaderSource, savingLinkageConfig } = this.state

    const modalButtons = [(
      <Button
        key="cancel"
        size="large"
        onClick={onCancel}
      >
        取 消
      </Button>
    ), (
      <Button
        key="submit"
        size="large"
        type="primary"
        loading={loading}
        disabled={loading}
        onClick={this.onSavingLinkageConfig}
      >
        保 存
      </Button>
    )]

    return (
      <Modal
        title="联动关系配置"
        wrapClassName="ant-modal-large"
        maskClosable={false}
        visible={visible}
        onCancel={onCancel}
        footer={modalButtons}
      >
        <div className={styles.modalLinkageConfig}>
          <LinkageConfig
            linkages={linkages}
            cascaderSource={linkageCascaderSource}
            onGetWidgetInfo={onGetWidgetInfo}
            saving={savingLinkageConfig}
            onSave={onSave}
          />
        </div>
      </Modal>
    )
  }
}

export default DashboardLinkageConfig
