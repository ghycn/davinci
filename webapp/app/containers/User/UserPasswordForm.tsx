

import * as React from 'react'

import { Form, Row, Col, Input } from 'antd'
const FormItem = Form.Item

const utilStyles = require('../../assets/less/util.less')

interface IUserPasswordFormProps {
  form: any
}

export class UserPasswordForm extends React.PureComponent<IUserPasswordFormProps, {}> {
  private checkPasswordConfirm = (rule, value, callback) => {
    if (value && value !== this.props.form.getFieldValue('newPass')) {
      callback('两次输入的密码不一致')
    } else {
      callback()
    }
  }

  private forceCheckConfirm = (rule, value, callback) => {
    const { form } = this.props
    if (form.getFieldValue('confirmPassword')) {
      form.validateFields(['confirmPassword'], { force: true })
    }
    callback()
  }

  public render () {
    const { getFieldDecorator } = this.props.form

    const commonFormItemStyle = {
      labelCol: { span: 8 },
      wrapperCol: { span: 14 }
    }

    return (
      <Form>
        <Row gutter={8}>
          <Col span={24}>
            <FormItem className={utilStyles.hide}>
              {getFieldDecorator('id', {})(
                <Input />
              )}
            </FormItem>
            <FormItem label="旧密码" {...commonFormItemStyle}>
              {getFieldDecorator('oldPass', {
                rules: [{
                  required: true,
                  message: '旧密码不能为空'
                }, {
                  min: 6,
                  max: 20,
                  message: '密码长度为6-20位'
                }]
              })(
                <Input type="password" placeholder="Your Password" />
              )}
            </FormItem>
          </Col>
          <Col span={24}>
            <FormItem label="新密码" {...commonFormItemStyle}>
              {getFieldDecorator('newPass', {
                rules: [{
                  required: true,
                  message: '新密码不能为空'
                }, {
                  min: 6,
                  max: 20,
                  message: '密码长度为6-20位'
                }, {
                  validator: this.forceCheckConfirm
                }]
              })(
                <Input type="password" placeholder="New Password" />
              )}
            </FormItem>
          </Col>
          <Col span={24}>
            <FormItem label="确认新密码" {...commonFormItemStyle}>
              {getFieldDecorator('confirmPassword', {
                rules: [{
                  required: true,
                  message: '请确认密码'
                }, {
                  validator: this.checkPasswordConfirm
                }]
              })(
                <Input type="password" placeholder="Confirm Password" />
              )}
            </FormItem>
          </Col>
        </Row>
      </Form>
    )
  }
}

export default Form.create()(UserPasswordForm)
