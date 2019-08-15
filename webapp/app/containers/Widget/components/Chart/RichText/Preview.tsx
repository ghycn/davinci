

import React from 'react'
import { getFormattedValue } from '../../util'
const Styles = require('./RichText.less')

interface IRichTextPreviewProps {
  content: string
  data: any[]
  fieldBoundaries: [string, string]
  mapFields: object
}

export class RichTextPreview extends React.PureComponent<IRichTextPreviewProps> {

  private renderFormattedText = () => {
    const { content, data, fieldBoundaries, mapFields } = this.props
    const [ prefix, suffix ] = fieldBoundaries
    const fieldRegx = new RegExp(`${prefix}(.+?)${suffix}`, 'g')

    const formattedText = content.replace(fieldRegx, (_, p1: string) => {
      if (!data.length || data[0][p1] === null) { return '' }
      let text = data.map((item) => item[p1]).join(', ')
      if (mapFields[p1]) {
        const config = mapFields[p1]
        text = getFormattedValue(text, config.format)
      }
      return text
    })
    return formattedText
  }

  public render () {
    return (
      <div className={Styles.content}>
        <div
          className="ql-editor"
          dangerouslySetInnerHTML={{__html: this.renderFormattedText()}}
        />
      </div>
    )
  }
}

export default RichTextPreview

