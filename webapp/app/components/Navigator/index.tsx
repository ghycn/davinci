

import React from 'react'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
import { Link } from 'react-router'
import classnames from 'classnames'
import DownloadList from '../DownloadList'

import {
  makeSelectLoginUser
} from '../../containers/App/selectors'

import { Dropdown, Menu, Icon } from 'antd'

const styles = require('./Navigator.less')

const goGithub = () => window.open('https://github.com/edp963/davinci')
const goDoc = () => window.open('https://edp963.github.io/davinci/')

interface INavigatorProps {
  show: boolean
  loginUser: object
  onLogout: () => void
}

export function Navigator (props: INavigatorProps) {
  const {
    show,
    onLogout
  } = props
  const headerClass = classnames({
    [styles.header]: true,
    [styles.hide]: !show
  })
  const menu = (
    <Menu>
      <Menu.Item key="0">
        <Link to="/account" >
          用户设置
        </Link>
      </Menu.Item>
      <Menu.Divider />
      <Menu.Item key="3">
        <a href="javascript:;" onClick={onLogout}>
          退出登录
        </a>
      </Menu.Item>
    </Menu>
  )

  return (
    <nav className={headerClass}>
      <div className={styles.logoPc}>
        <div className={styles.logo}>
          <Link to="/projects">
            <img src={require('../../assets/images/logo.png')} />
          </Link>
        </div>
      </div>
      <div className={styles.logoMobile}>
        <div className={styles.logo}>
          <Link to="/projects">
            <img src={require('../../assets/images/logo_mobile.png')} />
          </Link>
        </div>
      </div>
      <ul className={styles.tools}>
        {/*<li>*/}
        {/*  <DownloadList />*/}
        {/*</li>*/}
        {/*<li>*/}
        {/*  <Icon type="file-text" onClick={goDoc} />*/}
        {/*</li>*/}
        {/*<li>*/}
        {/*  <Icon type="github" onClick={goGithub}/>*/}
        {/*</li>*/}
        <li>
          <Dropdown overlay={menu} trigger={['click']} placement="bottomCenter">
            <Icon type="user" />
          </Dropdown>
        </li>
      </ul>
    </nav>
  )
}

const mapStateToProps = createStructuredSelector({
  loginUser: makeSelectLoginUser()
})

export default connect(mapStateToProps, null)(Navigator)
