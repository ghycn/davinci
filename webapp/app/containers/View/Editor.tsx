

import React from 'react'
import { compose, Dispatch } from 'redux'
import { connect } from 'react-redux'
import { createStructuredSelector } from 'reselect'
import memoizeOne from 'memoize-one'
import Helmet from 'react-helmet'
import { RouteComponentProps } from 'react-router'

import injectReducer from 'utils/injectReducer'
import injectSaga from 'utils/injectSaga'
import reducer from './reducer'
import sagas from './sagas'
import reducerSource from 'containers/Source/reducer'
import sagasSource from 'containers/Source/sagas'
import reducerProject from 'containers/Projects/reducer'
import sagasProject from 'containers/Projects/sagas'

import { IRouteParams } from 'app/routes'
import { hideNavigator } from '../App/actions'
import { ViewActions, ViewActionType } from './actions'
import { SourceActions, SourceActionType } from 'containers/Source/actions'
import {
  makeSelectEditingView,
  makeSelectEditingViewInfo,
  makeSelectSources,
  makeSelectSchema,
  makeSelectSqlDataSource,
  makeSelectSqlLimit,
  makeSelectSqlValidation,
  makeSelectLoading,

  makeSelectChannels,
  makeSelectTenants,
  makeSelectBizs
} from './selectors'

import { loadProjectRoles } from 'containers/Organizations/actions'
import { makeSelectProjectRoles } from 'containers/Projects/selectors'

import {
  IView, IViewModel, IViewRoleRaw, IViewRole, IViewVariable, IViewInfo,
  IExecuteSqlParams, IExecuteSqlResponse, IViewLoading, ISqlValidation,
  IDacChannel, IDacTenant, IDacBiz } from './types'
import { ISource, ISchema } from '../Source/types'
import { ViewVariableTypes } from './constants'

import { message } from 'antd'
import EditorSteps from './components/EditorSteps'
import EditorContainer from './components/EditorContainer'
import ModelAuth from './components/ModelAuth'
import SourceTable from './components/SourceTable'
import SqlEditor from './components/SqlEditor'
import SqlPreview from './components/SqlPreview'
import EditorBottom from './components/EditorBottom'
import ViewVariableList from './components/ViewVariableList'
import VariableModal from './components/VariableModal'

import Styles from './View.less'

interface IViewEditorStateProps {
  editingView: IView
  editingViewInfo: IViewInfo
  sources: ISource[]
  schema: ISchema
  sqlDataSource: IExecuteSqlResponse
  sqlLimit: number
  sqlValidation: ISqlValidation
  loading: IViewLoading
  projectRoles: any[]

  channels: IDacChannel[]
  tenants: IDacTenant[]
  bizs: IDacBiz[]
}

interface IViewEditorDispatchProps {
  onHideNavigator: () => void
  onLoadViewDetail: (viewId: number) => void
  onLoadSources: (projectId: number) => void
  onLoadSourceDatabases: (sourceId: number) => void
  onLoadDatabaseTables: (sourceId: number, databaseName: string) => void
  onLoadTableColumns: (sourceId: number, databaseName: string, tableName: string) => void
  onExecuteSql: (params: IExecuteSqlParams) => void
  onAddView: (view: IView, resolve: () => void) => void
  onEditView: (view: IView, resolve: () => void) => void
  onUpdateEditingView: (view: IView) => void
  onUpdateEditingViewInfo: (viewInfo: IViewInfo) => void
  onSetSqlLimit: (limit: number) => void

  onLoadDacChannels: () => void,
  onLoadDacTenants: (channelName: string) => void,
  onLoadDacBizs: (channelName: string, tenantId: number) => void,

  onResetState: () => void
  onLoadProjectRoles: (projectId: number) => void
}

type IViewEditorProps = IViewEditorStateProps & IViewEditorDispatchProps & RouteComponentProps<{}, IRouteParams>

interface IViewEditorStates {
  containerHeight: number
  sqlValidationCode: number
  init: boolean
  currentStep: number
  nextDisabled: boolean
}


export class ViewEditor extends React.Component<IViewEditorProps, IViewEditorStates> {

  public state: Readonly<IViewEditorStates> = {
    containerHeight: 0,
    currentStep: 0,
    sqlValidationCode: null,
    init: true,
    nextDisabled: true
  }

  public constructor (props: IViewEditorProps) {
    super(props)
    const { onLoadSources, onLoadViewDetail, onLoadProjectRoles, onLoadDacChannels, params } = this.props
    const { viewId, pid: projectId } = params
    if (projectId) {
      onLoadSources(+projectId)
      onLoadProjectRoles(+projectId)
    }
    if (viewId) {
      onLoadViewDetail(+viewId)
    }
    onLoadDacChannels()
  }

  public static getDerivedStateFromProps:
    React.GetDerivedStateFromProps<IViewEditorProps, IViewEditorStates>
  = (props, state) => {
    const { params, editingView, sqlValidation } = props
    const { viewId } = params
    const { init, sqlValidationCode } = state
    let nextDisabled = state.nextDisabled
    if (sqlValidationCode !== sqlValidation.code && sqlValidation.code) {
      message.destroy()
      message.open({
        content: `Syntax check ${sqlValidation.message}`,
        type: sqlValidation.code === 200 ? 'success' : 'error',
        duration: 5
      })
      nextDisabled = (sqlValidation.code !== 200)
    }
    if (editingView && editingView.id === +viewId) {
      if (init) {
        props.onLoadSourceDatabases(editingView.sourceId)
        ViewEditor.ExecuteSql(props)
        return {
          init: false,
          sqlValidationCode: sqlValidation.code,
          nextDisabled
        }
      }
    } else {
      return {
        sqlValidationCode: sqlValidation.code,
        nextDisabled
      }
    }
    return { sqlValidationCode: sqlValidation.code, nextDisabled }
  }

  public componentDidMount () {
    this.props.onHideNavigator()

  }

  public componentWillUnmount () {
    this.props.onResetState()
  }

  private executeSql = () => {
    ViewEditor.ExecuteSql(this.props)
  }

  private static ExecuteSql = (props: IViewEditorProps) => {
    const { onExecuteSql, editingView, editingViewInfo, sqlLimit } = props
    const { sourceId, sql } = editingView
    const { variable } = editingViewInfo
    const updatedParams: IExecuteSqlParams = {
      sourceId,
      sql,
      limit: sqlLimit,
      variables: variable
    }
    onExecuteSql(updatedParams)
  }

  private stepChange = (step: number) => {
    const { currentStep } = this.state
    if (currentStep + step < 0) {
      this.goToViewList()
      return
    }
    const { editingView } = this.props
    const { name, sourceId, sql } = editingView
    const errorMessages = ['名称不能为空', '请选择数据源', 'sql 不能为空']
    const fieldsValue = [name, sourceId, sql]
    const hasError = fieldsValue.some((val, idx) => {
      if (!val) {
        message.error(errorMessages[idx])
        return true
      }
    })
    if (hasError) { return }
    this.setState({ currentStep: currentStep + step }, () => {
      if (this.state.currentStep > 1) {
        this.saveView()
      }
    })
  }

  private saveView = () => {
    const { onAddView, onEditView, editingView, editingViewInfo, projectRoles, params } = this.props
    const { pid: projectId } = params
    const { model, variable, roles } = editingViewInfo
    const { id: viewId } = editingView
    const validRoles = roles.filter(({ roleId }) => projectRoles && projectRoles.findIndex(({ id }) => id === roleId) >= 0)
    const updatedView: IView = {
      ...editingView,
      projectId: +projectId,
      model: JSON.stringify(model),
      variable: JSON.stringify(variable),
      roles: validRoles.map<IViewRoleRaw>(({ roleId, columnAuth, rowAuth }) => {
        const validColumnAuth = columnAuth.filter((c) => !!model[c])
        const validRowAuth = rowAuth.filter((r) => {
          const v = variable.find((v) => v.name === r.name)
          if (!v) { return false }
          return (v.type === ViewVariableTypes.Authorization && !v.fromService)
        })
        return {
          roleId,
          columnAuth: JSON.stringify(validColumnAuth),
          rowAuth: JSON.stringify(validRowAuth)
        }
      })
    }
    viewId ? onEditView(updatedView, this.goToViewList) : onAddView(updatedView, this.goToViewList)
  }

  private goToViewList = () => {
    const { router, params } = this.props
    const { pid: projectId } = params
    router.push(`/project/${projectId}/views`)
  }

  private viewChange = (propName: keyof IView, value: string | number) => {
    const { editingView, onUpdateEditingView } = this.props
    const nextDisabled = (propName === 'sql' && value !== editingView.sql)
      ? true
      : this.state.nextDisabled
    this.setState({ nextDisabled })
    const updatedView = {
      ...editingView,
      [propName]: value
    }
    onUpdateEditingView(updatedView)
  }

  private sqlChange = (sql: string) => {
    this.viewChange('sql', sql)
  }

  private modelChange = (partialModel: IViewModel) => {
    const { editingViewInfo, onUpdateEditingViewInfo } = this.props
    const { model } = editingViewInfo
    const updatedViewInfo: IViewInfo = {
      ...editingViewInfo,
      model: { ...model, ...partialModel }
    }
    onUpdateEditingViewInfo(updatedViewInfo)
  }

  private variableChange = (updatedVariable: IViewVariable[]) => {
    const { editingViewInfo, onUpdateEditingViewInfo } = this.props
    const updatedViewInfo: IViewInfo = {
      ...editingViewInfo,
      variable: updatedVariable
    }
    onUpdateEditingViewInfo(updatedViewInfo)
  }

  private viewRoleChange = (viewRole: IViewRole) => {
    const { editingViewInfo, onUpdateEditingViewInfo } = this.props
    const { roles } = editingViewInfo
    const updatedRoles = roles.filter((role) => role.roleId !== viewRole.roleId)
    updatedRoles.push(viewRole)
    const updatedViewInfo = {
      ...editingViewInfo,
      roles: updatedRoles
    }
    onUpdateEditingViewInfo(updatedViewInfo)
  }

  private getSqlHints = memoizeOne((sourceId: number, schema: ISchema, variables: IViewVariable[]) => {
    if (!sourceId) { return {} }

    const variableHints = variables.reduce((acc, v) => {
      acc[`$${v.name}$`] = []
      return acc
    }, {})
    const { mapDatabases, mapTables, mapColumns } = schema
    if (!mapDatabases[sourceId]) { return {} }

    const tableHints: { [tableName: string]: string[] } = Object.values(mapTables).reduce((acc, tablesInfo) => {
      if (tablesInfo.sourceId !== sourceId) { return acc }

      tablesInfo.tables.forEach(({ name: tableName }) => {
        acc[tableName] = []
      })
      return acc
    }, {})

    Object.values(mapColumns).forEach((columnsInfo) => {
      if (columnsInfo.sourceId !== sourceId) { return }
      const { tableName, columns } = columnsInfo
      if (tableHints[tableName]) {
        tableHints[tableName] = tableHints[tableName].concat(columns.map((col) => col.name))
      }
    })

    const hints = {
      ...variableHints,
      ...tableHints
    }
    return hints
  })

  public render () {
    const {
      sources, schema,
      sqlDataSource, sqlLimit, loading, projectRoles,
      channels, tenants, bizs,
      editingView, editingViewInfo,
      onLoadSourceDatabases, onLoadDatabaseTables, onLoadTableColumns, onSetSqlLimit,
      onLoadDacTenants, onLoadDacBizs } = this.props
    const { currentStep, nextDisabled } = this.state
    const { model, variable, roles: viewRoles } = editingViewInfo
    const sqlHints = this.getSqlHints(editingView.sourceId, schema, variable)
    const containerVisible = !currentStep
    const modelAuthVisible = !!currentStep

    return (
      <>
        <Helmet title="View" />
        <div className={Styles.viewEditor}>
          <div className={Styles.header}>
            <div className={Styles.steps}>
              <EditorSteps current={currentStep} />
            </div>
          </div>
          <EditorContainer
            visible={containerVisible}
            variable={variable}
            onVariableChange={this.variableChange}
          >
            <SourceTable
              view={editingView}
              sources={sources}
              schema={schema}
              onViewChange={this.viewChange}
              onSourceSelect={onLoadSourceDatabases}
              onDatabaseSelect={onLoadDatabaseTables}
              onTableSelect={onLoadTableColumns}
            />
            <SqlEditor value={editingView.sql} hints={sqlHints} onSqlChange={this.sqlChange} />
            <SqlPreview size="small" loading={loading.execute} response={sqlDataSource} />
            <EditorBottom
              sqlLimit={sqlLimit}
              loading={loading.execute}
              nextDisabled={nextDisabled}
              onSetSqlLimit={onSetSqlLimit}
              onExecuteSql={this.executeSql}
              onStepChange={this.stepChange}
            />
            <ViewVariableList variables={variable} />
            <VariableModal
              channels={channels}
              tenants={tenants}
              bizs={bizs}
              onLoadDacTenants={onLoadDacTenants}
              onLoadDacBizs={onLoadDacBizs}
            />
          </EditorContainer>
          <ModelAuth
            visible={modelAuthVisible}
            model={model}
            variable={variable}
            sqlColumns={sqlDataSource.columns}
            roles={projectRoles}
            viewRoles={viewRoles}
            onModelChange={this.modelChange}
            onViewRoleChange={this.viewRoleChange}
            onStepChange={this.stepChange}
          />
        </div>
      </>
    )
  }
}

const mapDispatchToProps = (dispatch: Dispatch<ViewActionType | SourceActionType | any>) => ({
  onHideNavigator: () => dispatch(hideNavigator()),
  onLoadViewDetail: (viewId: number) => dispatch(ViewActions.loadViewsDetail([viewId])),
  onLoadSources: (projectId) => dispatch(SourceActions.loadSources(projectId)),
  onLoadSourceDatabases: (sourceId) => dispatch(SourceActions.loadSourceDatabases(sourceId)),
  onLoadDatabaseTables: (sourceId, databaseName) => dispatch(SourceActions.loadDatabaseTables(sourceId, databaseName)),
  onLoadTableColumns: (sourceId, databaseName, tableName) => dispatch(SourceActions.loadTableColumns(sourceId, databaseName, tableName)),
  onExecuteSql: (params) => dispatch(ViewActions.executeSql(params)),
  onAddView: (view, resolve) => dispatch(ViewActions.addView(view, resolve)),
  onEditView: (view, resolve) => dispatch(ViewActions.editView(view, resolve)),
  onUpdateEditingView: (view) => dispatch(ViewActions.updateEditingView(view)),
  onUpdateEditingViewInfo: (viewInfo: IViewInfo) => dispatch(ViewActions.updateEditingViewInfo(viewInfo)),
  onSetSqlLimit: (limit: number) => dispatch(ViewActions.setSqlLimit(limit)),

  onLoadDacChannels: () => dispatch(ViewActions.loadDacChannels()),
  onLoadDacTenants: (channelName) => dispatch(ViewActions.loadDacTenants(channelName)),
  onLoadDacBizs: (channelName, tenantId) => dispatch(ViewActions.loadDacBizs(channelName, tenantId)),

  onResetState: () => dispatch(ViewActions.resetViewState()),
  onLoadProjectRoles: (projectId) => dispatch(loadProjectRoles(projectId))
})

const mapStateToProps = createStructuredSelector({
  editingView: makeSelectEditingView(),
  editingViewInfo: makeSelectEditingViewInfo(),
  sources: makeSelectSources(),
  schema: makeSelectSchema(),
  sqlDataSource: makeSelectSqlDataSource(),
  sqlLimit: makeSelectSqlLimit(),
  sqlValidation: makeSelectSqlValidation(),
  loading: makeSelectLoading(),
  projectRoles: makeSelectProjectRoles(),

  channels: makeSelectChannels(),
  tenants: makeSelectTenants(),
  bizs: makeSelectBizs()
})

const withConnect = connect(mapStateToProps, mapDispatchToProps)
const withReducer = injectReducer({ key: 'view', reducer })
const withSaga = injectSaga({ key: 'view', saga: sagas })
const withReducerSource = injectReducer({ key: 'source', reducer: reducerSource })
const withSagaSource = injectSaga({ key: 'source', saga: sagasSource })
const withReducerProject = injectReducer({ key: 'project', reducer: reducerProject })
const withSagaProject = injectSaga({ key: 'project', saga: sagasProject })

export default compose(
  withReducer,
  withReducerSource,
  withSaga,
  withSagaSource,
  withReducerProject,
  withSagaProject,
  withConnect
)(ViewEditor)
