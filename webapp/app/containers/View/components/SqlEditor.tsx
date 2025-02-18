

import React from 'react'
import debounce from 'lodash/debounce'

import Styles from '../View.less'

interface ISqlEditorProps {
  hints: {
    [name: string]: []
  }
  value: string
  onSqlChange: (sql: string) => void
}

export class SqlEditor extends React.PureComponent<ISqlEditorProps> {

  private sqlEditorContainer = React.createRef<HTMLTextAreaElement>()
  private sqlEditor
  private debouncedSqlChange = debounce((val: string) => { this.props.onSqlChange(val) }, 500)

  constructor (props) {
    super(props)

    require([
      'codemirror/lib/codemirror',
      'codemirror/lib/codemirror.css',
      'assets/override/codemirror_theme.css',
      'codemirror/addon/hint/show-hint.css',
      'codemirror/addon/edit/matchbrackets',
      'codemirror/mode/sql/sql',
      'codemirror/addon/hint/show-hint',
      'codemirror/addon/hint/sql-hint',
      'codemirror/addon/display/placeholder'
    ], (CodeMirror) => {
      this.initEditor(CodeMirror, props.value)
    })
  }

  public componentDidUpdate () {
    if (this.sqlEditor) {
      const { value } = this.props
      const localValue = this.sqlEditor.doc.getValue()
      if (value !== localValue) {
        this.sqlEditor.doc.setValue(this.props.value)
      }
    }
  }

  private initEditor = (codeMirror, value: string) => {
    const { fromTextArea } = codeMirror
    const config = {
      mode: 'text/x-sql',
      theme: '3024-day',
      lineNumbers: true,
      lineWrapping: true,
      autoCloseBrackets: true,
      matchBrackets: true,
      foldGutter: true
    }
    this.sqlEditor = fromTextArea(this.sqlEditorContainer.current, config)
    this.sqlEditor.doc.setValue(value)
    this.sqlEditor.on('change', (_: CodeMirror.Editor, change: CodeMirror.EditorChange) => {
      this.debouncedSqlChange(_.getDoc().getValue())

      if (change.origin === '+input'
          && change.text[0] !== ';'
          && change.text[0].trim() !== ''
          && change.text[1] !== '') {
        this.sqlEditor.showHint({
          completeSingle: false,
          tables: this.props.hints
        })
      }
    })
  }

  public render () {
    return (
      <div className={Styles.sqlEditor}>
        <textarea ref={this.sqlEditorContainer} />
      </div>
    )
  }
}

export default SqlEditor
