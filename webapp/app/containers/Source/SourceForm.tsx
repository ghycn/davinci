

import React from 'react'
import { ISource } from './types'

import { Modal, Form, Row, Col, Button, Input, Select, Icon } from 'antd'
const FormItem = Form.Item
const TextArea = Input.TextArea
const Option = Select.Option
import { FormComponentProps } from 'antd/lib/form/Form'

const utilStyles = require('../../assets/less/util.less')

interface ISourceFormProps {
  visible: boolean
  formLoading: boolean
  testLoading: boolean
  source: ISource
  onSave: (values: any) => void
  onClose: () => void
  onTestSourceConnection: (username: string, password: string, jdbcUrl: string) => any
  onCheckUniqueName: (pathname: string, data: any, resolve: () => any, reject: (error: string) => any) => any
}

export class SourceForm extends React.PureComponent<ISourceFormProps & FormComponentProps> {

  private commonFormItemStyle = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 }
  }

  public componentDidUpdate (prevProps: ISourceFormProps & FormComponentProps) {
    const { form, source, visible } = this.props
    if (source !== prevProps.source || visible !== prevProps.visible) {
      form.setFieldsValue(source)
    }
  }

  public checkNameUnique = (rule, name = '', callback) => {
    const { onCheckUniqueName, source } = this.props
    const { id, projectId } = source

    const data = { id, name, projectId }
    if (!name) {
      callback()
    }
    onCheckUniqueName('source', data,
      () => {
        callback()
      }, (err) => {
        callback(err)
      })
  }

  private testSourceConnection = () => {
    const { config } = this.props.form.getFieldsValue() as ISource
    const { username, password, url } = config
    this.props.onTestSourceConnection(username, password, url)
  }

  private save = () => {
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        this.props.onSave(values)
      }
    })
  }

  private reset = () => {
    this.props.form.resetFields()
  }

  public render () {
    const {
      source,
      visible,
      formLoading,
      testLoading,
      form,
      onClose
    } = this.props
    if (!source) { return null }
    const { id: sourceId } = source
    const { getFieldDecorator } = form

    const modalButtons = ([(
      <Button
        key="submit"
        size="large"
        type="primary"
        loading={formLoading}
        disabled={formLoading}
        onClick={this.save}
      >
        保 存
      </Button>),
      (
      <Button
        key="back"
        size="large"
        onClick={onClose}
      >
        取 消
      </Button>)
    ])

    return (
      <Modal
        title={`${!sourceId ? '新增' : '修改'} Source`}
        wrapClassName="ant-modal-small"
        maskClosable={false}
        visible={visible}
        footer={modalButtons}
        onCancel={onClose}
        afterClose={this.reset}
      >
        <Form>
          <Row gutter={8}>
            <Col span={24}>
              <FormItem className={utilStyles.hide}>
                {getFieldDecorator<ISource>('id')(
                  <Input />
                )}
              </FormItem>
              <FormItem label="名称" {...this.commonFormItemStyle} hasFeedback>
                {getFieldDecorator<ISource>('name', {
                  rules: [{
                    required: true,
                    message: 'Name 不能为空'
                  }, {
                    validator: this.checkNameUnique
                  }]
                })(
                  <Input autoComplete="off" placeholder="Name" />
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="类型" {...this.commonFormItemStyle}>
                {getFieldDecorator<ISource>('type', {
                  initialValue: 'jdbc'
                })(
                  <Select>
                    <Option value="jdbc">JDBC</Option>
                    <Option value="csv">CSV文件</Option>
                  </Select>
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="用户名" {...this.commonFormItemStyle}>
                {getFieldDecorator('config.username', {
                  // rules: [{
                  //   required: true,
                  //   message: 'User 不能为空'
                  // }],
                  initialValue: ''
                })(
                  <Input autoComplete="off" placeholder="User" />
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="密码" {...this.commonFormItemStyle}>
                {getFieldDecorator('config.password', {
                  // rules: [{
                  //   required: true,
                  //   message: 'Password 不能为空'
                  // }],
                  initialValue: ''
                })(
                  <Input autoComplete="off" placeholder="Password" type="password" />
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="连接Url" {...this.commonFormItemStyle}>
                {getFieldDecorator('config.url', {
                  rules: [{
                    required: true,
                    message: 'Url 不能为空'
                  }],
                  initialValue: ''
                })(
                  <Input
                    placeholder="Connection Url"
                    autoComplete="off"
                    addonAfter={
                      testLoading
                        ? <Icon type="loading" />
                        : <span onClick={this.testSourceConnection} style={{cursor: 'pointer'}}>点击测试</span>
                    }
                  />
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="描述" {...this.commonFormItemStyle}>
                {getFieldDecorator('description', {
                  initialValue: ''
                })(
                  <TextArea
                    placeholder="Description"
                    autosize={{minRows: 2, maxRows: 6}}
                  />
                )}
              </FormItem>
            </Col>
            <Col span={24}>
              <FormItem label="配置信息" {...this.commonFormItemStyle}>
                {getFieldDecorator('config.parameters', {
                  initialValue: ''
                })(
                  <TextArea
                    placeholder="Config"
                    autosize={{minRows: 2, maxRows: 6}}
                  />
                )}
              </FormItem>
            </Col>
          </Row>
        </Form>
      </Modal>
    )
  }
}

export default Form.create<ISourceFormProps & FormComponentProps>()(SourceForm)

