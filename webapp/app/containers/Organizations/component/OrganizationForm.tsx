

import React from 'react'
import { Form, Row, Col, Input, Button } from 'antd'
const FormItem = Form.Item
const TextArea = Input.TextArea
const styles = require('../Organization.less')
const utilStyles = require('../../../assets/less/util.less')

interface IProjectsFormProps {
  form: any
  modalLoading: boolean
  onModalOk: () => any
  onCheckUniqueName: (pathname: any, data: any, resolve: () => any, reject: (error: string) => any) => any
}

export class OrganizationForm extends React.PureComponent<IProjectsFormProps, {}> {
  public render () {
    const { getFieldDecorator } = this.props.form
    const { modalLoading } = this.props
    const commonFormItemStyle = {
      labelCol: { span: 3 },
      wrapperCol: { span: 24}
    }
    const modalButtons = [(
      <Button
        key="submit"
        type="primary"
        loading={modalLoading}
        disabled={modalLoading}
        onClick={this.props.onModalOk}
      >
        保 存
      </Button>
    )]
    return (
      <div className={styles.formWrapper}>
        <div className={styles.header}>
          <div className={styles.title}>
            创建组织
          </div>
          <div className={styles.desc}>
            {/* 用户创建组织，邀请成员加入，创建团队 */}
          </div>
        </div>
        <div className={styles.body}>
          <Form>
            <Row gutter={8}>
              <Col span={24}>
                <FormItem label="名称" {...commonFormItemStyle}>
                  {getFieldDecorator('name', {
                    rules: [{
                      required: true,
                      message: 'Name 不能为空'
                    }, {
                      validator: this.props.onCheckUniqueName
                    }],
                    validateFirst: true
                  })(
                    <Input placeholder="Name" />
                  )}
                </FormItem>
              </Col>
              <Col span={24}>
                <FormItem label="描述" {...commonFormItemStyle}>
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
            </Row>
          </Form>
        </div>
        <div className={styles.footer}>
          {modalButtons}
        </div>
      </div>
    )
  }
}


export default Form.create()((OrganizationForm))
