

import * as React from 'react'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'

import Navigator from '../../components/Navigator'

import { logged, logout, setLoginUser, getLoginUser, loadDownloadList } from '../App/actions'
import { makeSelectLogged, makeSelectNavigator } from '../App/selectors'
import { promiseDispatcher } from '../../utils/reduxPromisation'
import checkLogin from '../../utils/checkLogin'
import { setToken } from '../../utils/request'
import { DOWNLOAD_LIST_POLLING_FREQUENCY } from 'app/globalConstants'

const styles = require('./Main.less')

interface IMainProps {
  params: {pid?: number}
  children: React.ReactNode
  router: any
  logged: boolean
  navigator: boolean
  onLogged: () => any
  onLogout: () => any
  onSetLoginUser: (user: object) => any
  onGetLoginUser: (resolve: () => void) => any
  onLoadDownloadList: () => void
}

export class Main extends React.Component<IMainProps, {}> {

  private downloadListPollingTimer: number

  public componentWillMount () {
    this.checkTokenLink()
  }

  public componentWillUnmount () {
    if (this.downloadListPollingTimer) {
      clearInterval(this.downloadListPollingTimer)
    }
  }

  private checkTokenLink = () => {
    const {
      router,
      onGetLoginUser
    } = this.props

    const qs = this.getQs()
    const token = qs['token']
    // TODO allow take other parameters
    // const dashboard = qs['dashboard']

    if (token) {
      setToken(token)
      localStorage.setItem('TOKEN', token)
      localStorage.setItem('TOKEN_EXPIRE', `${new Date().getTime() + 3600000}`)
      onGetLoginUser(() => {
        router.replace('/projects')
        // if (dashboard) {
        //   router.replace(`/project/${this.props.params.pid}/dashboard/${dashboard}`)
        // } else {

        // }
      })
      this.initPolling()
    } else {
      this.checkNormalLogin()
    }
  }

  private checkNormalLogin = () => {
    if (checkLogin()) {
      const token = localStorage.getItem('TOKEN')
      const loginUser = localStorage.getItem('loginUser')
      setToken(token)
      this.props.onLogged()
      this.props.onSetLoginUser(JSON.parse(loginUser))
      this.initPolling()
    } else {
      this.props.router.replace('/login')
    }
  }

  private getQs = () => {
    const search = location.search
    const qs = search ? search.substr(1) : ''
    if (qs) {
      return qs
        .split('&')
        .reduce((rdc, val) => {
          const pair = val.split('=')
          rdc[pair[0]] = pair[1]
          return rdc
        }, {})
    } else {
      return false
    }
  }

  private initPolling = () => {
    this.props.onLoadDownloadList()
    this.downloadListPollingTimer = window.setInterval(() => {
      this.props.onLoadDownloadList()
    }, DOWNLOAD_LIST_POLLING_FREQUENCY)
  }

  private logout = () => {
    const {
      router,
      onLogout
    } = this.props
    onLogout()
    localStorage.removeItem('TOKEN')
    localStorage.removeItem('TOKEN_EXPIRE')
    router.replace('/login')
  }

  public render () {
    const { logged, navigator, children } = this.props

    return logged
      ? (
        <div className={styles.container}>
          <Navigator
            show={navigator}
            onLogout={this.logout}
          />
          {children}
        </div>
      )
      : (
        <div />
      )
  }
}

const mapStateToProps = createStructuredSelector({
  logged: makeSelectLogged(),
  navigator: makeSelectNavigator()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLogged: () => promiseDispatcher(dispatch, logged),
    onLogout: () => promiseDispatcher(dispatch, logout),
    onSetLoginUser: (user) => promiseDispatcher(dispatch, setLoginUser, user),
    onGetLoginUser: (resolve) => dispatch(getLoginUser(resolve)),
    onLoadDownloadList: () => dispatch(loadDownloadList())
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(Main)
