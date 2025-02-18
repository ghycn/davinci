

import { ActionTypes } from './constants'
import { returnType } from 'utils/redux'
import { ISourceBase, ISource, ITableColumns, ISourceDatabases, ICSVMetaInfo, IDatabaseTables } from './types'

export const SourceActions = {
  loadSources (projectId: number) {
    return {
      type: ActionTypes.LOAD_SOURCES,
      payload: {
        projectId
      }
    }
  },
  sourcesLoaded (sources: ISourceBase[]) {
    return {
      type: ActionTypes.LOAD_SOURCES_SUCCESS,
      payload: {
        sources
      }
    }
  },
  loadSourcesFail () {
    return {
      type: ActionTypes.LOAD_SOURCES_FAILURE,
      payload: {}
    }
  },

  loadSourceDetail (sourceId: number, resolve?: (source: ISource) => void) {
    return {
      type: ActionTypes.LOAD_SOURCE_DETAIL,
      payload: {
        sourceId,
        resolve
      }
    }
  },
  sourceDetailLoaded (source: ISource) {
    return {
      type: ActionTypes.LOAD_SOURCE_DETAIL_SUCCESS,
      payload: {
        source
      }
    }
  },
  loadSourceDetailFail () {
    return {
      type: ActionTypes.LOAD_SOURCE_DETAIL_FAIL,
      payload: {}
    }
  },

  addSource (source: ISource, resolve: () => void) {
    return {
      type: ActionTypes.ADD_SOURCE,
      payload: {
        source,
        resolve
      }
    }
  },
  sourceAdded (result: ISourceBase) {
    return {
      type: ActionTypes.ADD_SOURCE_SUCCESS,
      payload: {
        result
      }
    }
  },
  addSourceFail () {
    return {
      type: ActionTypes.ADD_SOURCE_FAILURE,
      payload: {}
    }
  },
  deleteSource (id: number) {
    return {
      type: ActionTypes.DELETE_SOURCE,
      payload: {
        id
      }
    }
  },
  sourceDeleted (id: number) {
    return {
      type: ActionTypes.DELETE_SOURCE_SUCCESS,
      payload: {
        id
      }
    }
  },
  deleteSourceFail () {
    return {
      type: ActionTypes.DELETE_SOURCE_FAILURE,
      payload: {}
    }
  },
  editSource (source: ISource, resolve: () => void) {
    return {
      type: ActionTypes.EDIT_SOURCE,
      payload: {
        source,
        resolve
      }
    }
  },
  sourceEdited (result: ISourceBase) {
    return {
      type: ActionTypes.EDIT_SOURCE_SUCCESS,
      payload: {
        result
      }
    }
  },
  editSourceFail () {
    return {
      type: ActionTypes.EDIT_SOURCE_FAILURE,
      payload: {}
    }
  },
  testSourceConnection (url: string) {
    return {
      type: ActionTypes.TEST_SOURCE_CONNECTION,
      payload: {
        url
      }
    }
  },
  sourceConnected () {
    return {
      type: ActionTypes.TEST_SOURCE_CONNECTION_SUCCESS,
      payload: {}
    }
  },
  testSourceConnectionFail () {
    return {
      type: ActionTypes.TEST_SOURCE_CONNECTION_FAILURE,
      payload: {}
    }
  },
  getCsvMetaId (csvMeta: ICSVMetaInfo, resolve: () => void) {
    return {
      type: ActionTypes.GET_CSV_META_ID,
      payload: {
        csvMeta,
        resolve
      }
    }
  },
  csvMetaIdGeted () {
    return {
      type: ActionTypes.GET_CSV_META_ID_SUCCESS,
      payload: {}
    }
  },
  getCsvMetaIdFail (error) {
    return {
      type: ActionTypes.GET_CSV_META_ID_FAILURE,
      payload: {
        error
      }
    }
  },

  loadSourceDatabases (sourceId: number) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASES,
      payload: {
        sourceId
      }
    }
  },
  sourceDatabasesLoaded (sourceDatabases: ISourceDatabases) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASES_SUCCESS,
      payload: {
        sourceDatabases
      }
    }
  },
  loadSourceDatabasesFail (err) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASES_FAILURE,
      payload: {
        err
      }
    }
  },

  loadDatabaseTables (sourceId: number, databaseName: string, resolve?) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASE_TABLES,
      payload: {
        sourceId,
        databaseName,
        resolve
      }
    }
  },
  databaseTablesLoaded (databaseTables: IDatabaseTables) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASE_TABLES_SUCCESS,
      payload: {
        databaseTables
      }
    }
  },
  loadDatabaseTablesFail (err) {
    return {
      type: ActionTypes.LOAD_SOURCE_DATABASE_TABLES_FAILURE,
      payload: {
        err
      }
    }
  },
  loadTableColumns (sourceId: number, databaseName: string, tableName: string, resolve?) {
    return {
      type: ActionTypes.LOAD_SOURCE_TABLE_COLUMNS,
      payload: {
        sourceId,
        databaseName,
        tableName,
        resolve
      }
    }
  },
  tableColumnsLoaded (databaseName: string, tableColumns: ITableColumns) {
    return {
      type: ActionTypes.LOAD_SOURCE_TABLE_COLUMNS_SUCCESS,
      payload: {
        databaseName,
        tableColumns
      }
    }
  },
  loadTableColumnsFail (err) {
    return {
      type: ActionTypes.LOAD_SOURCE_TABLE_COLUMNS_FAILURE,
      payload: {
        err
      }
    }
  }
}

const mockAction = returnType(SourceActions)
export type SourceActionType = typeof mockAction

export default SourceActions

