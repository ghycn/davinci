

import * as React from 'react'
import Helmet from 'react-helmet'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'

import LoginForm from './LoginForm'
import { Icon } from 'antd'

import { compose } from 'redux'
import injectReducer from '../../utils/injectReducer'
import injectSaga from '../../utils/injectSaga'
// import reducer from '../App/reducer'
// import saga from '../App/sagas'

import { login, logged, setLoginUser } from '../App/actions'
import { makeSelectLoginLoading } from '../App/selectors'
import { promiseDispatcher } from '../../utils/reduxPromisation'
import checkLogin from '../../utils/checkLogin'
import { setToken } from '../../utils/request'


const styles = require('./Login.less')

interface ILoginProps {
  router: any
  loginLoading: boolean
  onLogin: (username: string, password: string, resolve: () => any) => any
  onLogged: () => any
  onSetLoginUser: (user: object) => any
}

interface ILoginStates {
  username: string
  password: string
}

export class Login extends React.PureComponent<ILoginProps, ILoginStates> {
  constructor (props) {
    super(props)
    this.state = {
      username: '',
      password: ''
    }
  }

  public componentWillMount () {
    this.checkNormalLogin()
  }

  private checkNormalLogin = () => {
    if (checkLogin()) {
      const token = localStorage.getItem('TOKEN')
      const loginUser = localStorage.getItem('loginUser')

      setToken(token)
      this.props.onLogged()
      this.props.onSetLoginUser(JSON.parse(loginUser))
      this.props.router.replace('/')
    }
  }

  private changeUsername = (e) => {
    this.setState({
      username: e.target.value.trim()
    })
  }

  private changePassword = (e) => {
    this.setState({
      password: e.target.value
    })
  }

  private toSignUp = () => {
    const { router } = this.props
    router.replace('/register')
  }

  private doLogin = () => {
    const { onLogin, router } = this.props
    const { username, password } = this.state

    if (username && password) {
      onLogin(username, password, () => { router.replace('/')})
    }
  }

  public render () {
    const { loginLoading } = this.props
    const { username, password } = this.state
    return (
      <div className={styles.window}>
        <Helmet title="Login" />
        <LoginForm
          username={username}
          password={password}
          onChangeUsername={this.changeUsername}
          onChangePassword={this.changePassword}
          onLogin={this.doLogin}
        />
        <button
          disabled={loginLoading}
          onClick={this.doLogin}
        >
          {
            loginLoading
              ? <Icon type="loading" />
              : ''
          }
          登 录
        </button>
        {/*<p className={styles.tips}>*/}
        {/*  <span>还没有账号？ </span>*/}
        {/*  <a href="javascript:;" onClick={this.toSignUp}>注册智汇BI账号</a>*/}
        {/*</p>*/}
      </div>
    )
  }
}

const mapStateToProps = createStructuredSelector({
  loginLoading: makeSelectLoginLoading()
})

export function mapDispatchToProps (dispatch) {
  return {
    onLogin: (username, password, resolve) => dispatch(login(username, password, resolve)),
    onLogged: () => promiseDispatcher(dispatch, logged),
    onSetLoginUser: (user) => promiseDispatcher(dispatch, setLoginUser, user)
  }
}

const withConnect = connect<{}, {}, ILoginProps>(mapStateToProps, mapDispatchToProps)
// const withReducer = injectReducer({ key: 'global', reducer })
// const withSaga = injectSaga({ key: 'global', saga })

export default compose(
//  withReducer,
//  withSaga,
 withConnect
)(Login)




