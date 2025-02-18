

import * as React from 'react'
import { Form, Row, Col, Input, Select, TreeSelect } from 'antd'
const Option = Select.Option
const FormItem = Form.Item
import { ButtonSize } from 'antd/lib/button'
const utilStyles =  require('../../assets/less/util.less')

interface IConfigFormProps {
  form: any
  type: string
  vizs: any
  dashboardTree: any[]
  treeSelect: () => any
  treeChange: () => any
  loadTreeData: () => any
  dashboardTreeValue: any
}

export class ConfigForm extends React.PureComponent<IConfigFormProps> {
  public render () {
    const { getFieldDecorator } = this.props.form
    const { dashboardTree, dashboardTreeValue, treeSelect, treeChange, loadTreeData, vizs } = this.props
    const commonFormItemStyle = {
      labelCol: { span: 4 },
      wrapperCol: { span: 18 }
    }
    const treeSelectProps = {
      size: 'large' as ButtonSize,
      dropdownStyle: { maxHeight: 400, overflow: 'auto' },
      treeCheckable: true,
      onChange: treeChange,
      onSelect: treeSelect,
      treeData: vizs,
      value: dashboardTreeValue,
   //   loadData: loadTreeData,
      showCheckedStrategy: TreeSelect.SHOW_PARENT,
      searchPlaceholder: '请选择要发送的 Dashboard 或 Display'
    }
    return (
      <Form>
        <Row>
          <Col>
            <FormItem
              label="主题"
              labelCol={{span: 2}}
              wrapperCol={{span: 21}}
            >
              {getFieldDecorator('subject', {
                rules: [{
                  required: true,
                  message: 'Name 不能为空'
                }]
              })(
                <Input placeholder="subject" />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <FormItem className={utilStyles.hide}>
              {getFieldDecorator('id', {
                hidden: this.props.type === 'add'
              })(
                <Input />
              )}
            </FormItem>
            <FormItem
              label="收件人"
              {...commonFormItemStyle}
            >
              {getFieldDecorator('to', {
                rules: [{
                  required: true,
                  message: 'Name 不能为空'
                }]
              })(
                <Input placeholder="to" />
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem
              label="抄送"
              {...commonFormItemStyle}
            >
              {getFieldDecorator('cc', {

              })(
                <Input placeholder="cc" />
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <Col span={12}>
            <FormItem
              label="密送"
              {...commonFormItemStyle}
            >
              {getFieldDecorator('bcc', {
                initialValue: ''
              })(
                <Input placeholder="bcc" />
              )}
            </FormItem>
          </Col>
          <Col span={12}>
            <FormItem
              label="文件类型"
              {...commonFormItemStyle}
            >
              {getFieldDecorator('type', {
                initialValue: 'image'
              })(
                <Select>
                  <Option value="excel">excel</Option>
                  <Option value="image">image</Option>
                </Select>
              )}
            </FormItem>
          </Col>
        </Row>
        <Row>
          <div className="ant-col-2 ant-form-item-label"><label className="" title="发送项">发送项</label></div>
          <Col span={21}>
            <TreeSelect {...treeSelectProps} />
          </Col>
        </Row>
      </Form>
    )
  }
}


export default Form.create()(ConfigForm)
