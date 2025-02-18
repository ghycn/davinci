

import * as React from 'react'
import { Icon } from 'antd'
const styles = require('../Login/Login.less')

interface IRegisterFormProps {
  username: string
  email: string
  password: string
  password2: string
  onChangeUsername: (e: any) => any
  onChangeEmail: (e: any) => any
  onChangePassword: (e: any) => any
  onChangePassword2: (e: any) => any
  onSignup: () => any
  onCheckName: (id: number, name: string, type: string, resolve?: (res: any) => any, reject?: (error: any) => any) => any
}

export class RegisterForm extends React.PureComponent<IRegisterFormProps, {}> {
  constructor (props) {
    super(props)
  }

  private enterLogin: (e: KeyboardEvent) => any = null

  public componentWillUnmount () {
    this.unbindDocumentKeypress()
  }

  private bindDocumentKeypress = () => {
    this.enterLogin = (e) => {
      if (e.keyCode === 13) {
        this.props.onSignup()
      }
    }
    document.addEventListener('keypress', this.enterLogin, false)
  }

  private unbindDocumentKeypress = () => {
    document.removeEventListener('keypress', this.enterLogin, false)
    this.enterLogin = null
  }

  public render () {
    const {
      username,
      email,
      password,
      password2,
      onChangeUsername,
      onChangeEmail,
      onChangePassword,
      onChangePassword2
    } = this.props
    return (
      <div className={styles.form}>
        <div className={styles.input}>
          <Icon type="user" />
          <input
            placeholder="用户名"
            value={username}
            onFocus={this.bindDocumentKeypress}
            onBlur={this.unbindDocumentKeypress}
            onChange={onChangeUsername}
          />
        </div>
        <div className={styles.input}>
          <Icon type="mail" />
          <input
            placeholder="邮箱"
            value={email}
            onFocus={this.bindDocumentKeypress}
            onBlur={this.unbindDocumentKeypress}
            onChange={onChangeEmail}
          />
        </div>
        <div className={styles.input}>
          <Icon type="unlock" />
          <input
            placeholder="密码"
            type="password"
            value={password}
            onFocus={this.bindDocumentKeypress}
            onBlur={this.unbindDocumentKeypress}
            onChange={onChangePassword}
          />
        </div>
        <div className={styles.input}>
          <Icon type="unlock" />
          <input
            placeholder="确认密码"
            type="password"
            value={password2}
            onFocus={this.bindDocumentKeypress}
            onBlur={this.unbindDocumentKeypress}
            onChange={onChangePassword2}
          />
        </div>
      </div>
    )
  }
}

export default RegisterForm
