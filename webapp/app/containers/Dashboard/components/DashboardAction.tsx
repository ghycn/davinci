

import * as React from 'react'
import { Icon, Tooltip, Popover } from 'antd'
import {IconProps} from 'antd/lib/icon'
const styles = require('../Dashboard.less')
import {IProject} from '../../Projects'
import ShareDownloadPermission from '../../Account/components/checkShareDownloadPermission'
import ModulePermission from '../../Account/components/checkModulePermission'

interface IDashboardActionProps {
  currentProject: IProject
  depth: number
  item: {
    id: number,
    type: number,
    name: string
  }
  splitWidth: number
  onInitOperateMore: (item: any, type: string) => any
  initChangeDashboard: (id: number) => any
}

interface IDashboardActionState {
  popoverVisible: boolean
}

export class DashboardAction extends React.PureComponent<IDashboardActionProps, IDashboardActionState> {
  constructor (props) {
    super(props)
    this.state = {
      popoverVisible: false
    }
  }

  private handleVisibleChange = (visible) => {
    this.setState({
      popoverVisible: visible
    })
  }

  private operateMore = (item, type) => (e) => {
    const { popoverVisible } = this.state
    const { onInitOperateMore } = this.props

    if (this.state.popoverVisible) {
      this.setState({
        popoverVisible: false
      })
    }
    onInitOperateMore(item, type)
  }

  public render () {
    const {
      currentProject,
      depth,
      item,
      initChangeDashboard,
      splitWidth
    } = this.props
    const { popoverVisible } = this.state

    const EditActionButton = ModulePermission<React.DetailedHTMLProps<React.HTMLAttributes<HTMLLIElement>, HTMLLIElement>>(currentProject, 'viz')(Li)
    const editAction = (
      <EditActionButton onClick={this.operateMore(item, 'edit')}>
        <Icon type="edit" /> 编辑
      </EditActionButton>
    )

    const DownloadButton = ShareDownloadPermission<React.DetailedHTMLProps<React.HTMLAttributes<HTMLLIElement>, HTMLLIElement>>(currentProject, 'download')(Li)

    const downloadAction = (
      <DownloadButton style={{cursor: 'pointer'}} onClick={this.operateMore(item, 'download')}>
        <Icon type="download" className={styles.swap} /> 下载
      </DownloadButton>
    )



    const moveAction = (
      <EditActionButton onClick={this.operateMore(item, 'move')}>
        <Icon type="swap" className={styles.swap} /> 移动
      </EditActionButton>
    )

    const DeleteActionButton = ModulePermission<React.DetailedHTMLProps<React.HTMLAttributes<HTMLLIElement>, HTMLLIElement>>(currentProject, 'viz', true)(Li)
    const deleteAction = (
      <DeleteActionButton onClick={this.operateMore(item, 'delete')}>
        <Icon type="delete" /> 删除
      </DeleteActionButton>
    )

    const ulActionAll = (
      <ul className={styles.menu}>
        <li>{editAction}</li>
        {/* <li>{downloadAction}</li> */}
        <li>{moveAction}</li>
        <li>{deleteAction}</li>
      </ul>
    )

    const ulActionPart = (
      <ul className={styles.menu}>
        <li>{editAction}</li>
        {/* <li>{downloadAction}</li> */}
        <li>{moveAction}</li>
      </ul>
    )

    const icon = (
      <Icon
        type="ellipsis"
        className={styles.itemAction}
        title="More"
      />
    )

    let ulPopover
    if (currentProject && currentProject.permission) {
      const currentPermission = currentProject.permission.vizPermission
      if (currentPermission === 0) {
        ulPopover = null
      } else {
        ulPopover = (
          <Popover
            placement="bottomRight"
            content={currentPermission === 2 ? ulActionPart : ulActionAll}
            trigger="click"
            visible={popoverVisible}
            onVisibleChange={this.handleVisibleChange}
          >
            {icon}
          </Popover>)
      }
    }

    const titleWidth = `${splitWidth - 60 - 18 * depth}px`

    return (
      <span className={styles.portalTreeItem}>
        <Tooltip placement="right" title={`名称：${item.name}`}>
          {
            item.type === 0
              ? <h4 className={styles.dashboardTitle} style={{ width: titleWidth }}>{item.name}</h4>
              : <span className={styles.dashboardTitle} style={{width: titleWidth}} onClick={initChangeDashboard(item.id)}>
                  <Icon type={`${item.type === 2 ? 'table' : 'dot-chart'}`} />
                  <span className={styles.itemName}>{item.name}</span>
                </span>
          }
          {ulPopover}
        </Tooltip>
      </span>
    )
  }
}

function Li (props: React.DetailedHTMLProps<React.HTMLAttributes<HTMLLIElement>, HTMLLIElement>) {
  return (
    <span {...props} >{props.children}</span>
  )
}

export default DashboardAction
