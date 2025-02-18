

import React from 'react'
import { Form, Row, Col, Input, Radio, Button, Tabs, Modal, Checkbox } from 'antd'
import { FormComponentProps } from 'antd/lib/form/Form'
const TextArea = Input.TextArea
const FormItem = Form.Item
const RadioGroup = Radio.Group
const TabPane = Tabs.TabPane
import { IDisplay } from './DisplayList'
const styles = require('../../Portal/Portal.less')
import {IExludeRoles} from '../../Portal/components/PortalList'
const utilStyles = require('../../../assets/less/util.less')

interface IDisplayFormModalProps {
  projectId: number
  display: IDisplay,
  visible: boolean
  loading: boolean
  type: 'add' | 'edit'
  onCheckName: (type, data, resolve, reject) => void
  onSave: (display, type: string) => void
  onCancel: () => void
  exludeRoles?: IExludeRoles[]
  onChangePermission: (scope: object, e: any) => any
}

export class DisplayFormModal extends React.PureComponent<IDisplayFormModalProps & FormComponentProps, {}> {
  public componentWillReceiveProps (nextProps: IDisplayFormModalProps & FormComponentProps) {
    const { form, display } = nextProps
    if (display !== this.props.display) {
      this.initFormValue(form, display)
    }
  }

  public componentDidMount () {
    const { form, display } = this.props
    this.initFormValue(form, display)
  }

  private initFormValue (form, display: IDisplay) {
    if (display) {
      form.setFieldsValue({ ...display })
    } else {
      form.resetFields()
    }
  }

  private checkNameUnique = (_, value = '', callback) => {
    const { projectId, onCheckName, type, form } = this.props
    const { id } = form.getFieldsValue() as any
    const typeName = 'display'
    if (!value) {
      callback()
    }
    onCheckName(typeName, {
      projectId,
      id,
      name: value
    },
      () => {
        callback()
      }, (err) => {
        callback(err)
      })
  }

  private onModalOk = () => {
    const { type, projectId, onSave } = this.props
    this.props.form.validateFieldsAndScroll((err, values) => {
      if (!err) {
        onSave({ ...values, projectId }, type)
      }
    })
  }

  private commonFormItemStyle = {
    labelCol: { span: 6 },
    wrapperCol: { span: 16 }
  }

  public render () {
    const { type, visible, loading, form, onCancel, exludeRoles } = this.props
    const { getFieldDecorator } = form
    const authControl = exludeRoles && exludeRoles.length ? exludeRoles.map((role) => (
      <div className={styles.excludeList} key={`${role.name}key`}>
        <Checkbox checked={role.permission} onChange={this.props.onChangePermission.bind(this, role)}/>
        <b>{role.name}</b>
      </div>
    )) : []
    const modalButtons = [(
      <Button
        key="back"
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
        onClick={this.onModalOk}
      >
        保 存
      </Button>
    )]

    return (
      <Modal
          title={`${type === 'add' ? '新增' : '修改'} Display`}
          wrapClassName="ant-modal-small"
          visible={visible}
          footer={modalButtons}
          onCancel={onCancel}
      >
        <Form>
          <Row gutter={8}>
            <Col span={24}>
              <FormItem className={utilStyles.hide}>
                {getFieldDecorator('projectId')(
                  <Input />
                )}
              </FormItem>
              <FormItem className={utilStyles.hide}>
                {getFieldDecorator('id')(
                  <Input />
                )}
              </FormItem>
              <Tabs defaultActiveKey="displayInfo">
                <TabPane tab="基本信息" key="displayInfo">
                  <Col span={24}>
                    <FormItem label="名称" {...this.commonFormItemStyle}>
                      {getFieldDecorator('name', {
                        rules: [{
                          required: true,
                          message: 'Name 不能为空'
                        }, {
                          validator: this.checkNameUnique
                        }]
                      })(
                        <Input placeholder="Name" />
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
                    <FormItem label="是否发布" {...this.commonFormItemStyle}>
                      {getFieldDecorator('publish', {
                        initialValue: true
                      })(
                        <RadioGroup>
                          <Radio value>发布</Radio>
                          <Radio value={false}>编辑</Radio>
                        </RadioGroup>
                      )}
                    </FormItem>
                    <FormItem className={utilStyles.hide}>
                      {getFieldDecorator('avatar')(
                        <Input />
                      )}
                    </FormItem>
                  </Col>
                </TabPane>
                <TabPane tab="权限管理" key="dislayControl">
                  {
                    authControl
                  }
                </TabPane>
              </Tabs>
            </Col>
          </Row>
        </Form>
      </Modal>
    )
  }
}

export default Form.create<IDisplayFormModalProps & FormComponentProps>()(DisplayFormModal)
