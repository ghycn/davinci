

import { ActionTypes } from './constants'
import { returnType } from 'utils/redux'
import { IDavinciResponse } from 'utils/request'
import {
  IViewBase, IView, IExecuteSqlParams, IExecuteSqlResponse, IViewInfo,
  IDacChannel, IDacTenant, IDacBiz
} from './types'
import { IDataRequestParams } from 'containers/Dashboard/Grid'
import { RenderType } from 'containers/Widget/components/Widget'
import { IDistinctValueReqeustParams } from 'app/components/Filters'

export const ViewActions = {
  viewsLoaded (views: IViewBase[]) {
    return {
      type: ActionTypes.LOAD_VIEWS_SUCCESS,
      payload: {
        views
      }
    }
  },
  loadViews (projectId: number, resolve?: (views: IViewBase[]) => void) {
    return {
      type: ActionTypes.LOAD_VIEWS,
      payload: {
        projectId,
        resolve
      }
    }
  },
  loadViewsFail () {
    return {
      type: ActionTypes.LOAD_VIEWS_FAILURE,
      payload: {}
    }
  },

  viewsDetailLoaded (views: IView[]) {
    return {
      type: ActionTypes.LOAD_VIEWS_DETAIL_SUCCESS,
      payload: {
        views
      }
    }
  },
  loadViewsDetail (viewIds: number[], resolve?: () => void) {
    return {
      type: ActionTypes.LOAD_VIEWS_DETAIL,
      payload: {
        viewIds,
        resolve
      }
    }
  },
  loadViewsDetailFail () {
    return {
      type: ActionTypes.LOAD_VIEWS_DETAIL_FAILURE,
      payload: {}
    }
  },

  addView (view: IView, resolve: () => void) {
    return {
      type: ActionTypes.ADD_VIEW,
      payload: {
        view,
        resolve
      }
    }
  },
  viewAdded (result: IView) {
    return {
      type: ActionTypes.ADD_VIEW_SUCCESS,
      payload: {
        result
      }
    }
  },
  addViewFail () {
    return {
      type: ActionTypes.ADD_VIEW_FAILURE,
      payload: {}
    }
  },

  editView (view: IView, resolve: () => void) {
    return {
      type: ActionTypes.EDIT_VIEW,
      payload: {
        view,
        resolve
      }
    }
  },
  viewEdited (result: IView) {
    return {
      type: ActionTypes.EDIT_VIEW_SUCCESS,
      payload: {
        result
      }
    }
  },
  editViewFail () {
    return {
      type: ActionTypes.EDIT_VIEW_FAILURE,
      payload: {}
    }
  },

  deleteView (id: number, resolve: (id: number) => void) {
    return {
      type: ActionTypes.DELETE_VIEW,
      payload: {
        id,
        resolve
      }
    }
  },
  viewDeleted (id: number) {
    return {
      type: ActionTypes.DELETE_VIEW_SUCCESS,
      payload: {
        id
      }
    }
  },
  deleteViewFail () {
    return {
      type: ActionTypes.DELETE_VIEW_FAILURE,
      payload: {}
    }
  },

  executeSql (params: IExecuteSqlParams) {
    return {
      type: ActionTypes.EXECUTE_SQL,
      payload: {
        params
      }
    }
  },
  sqlExecuted (result: IDavinciResponse<IExecuteSqlResponse>) {
    return {
      type: ActionTypes.EXECUTE_SQL_SUCCESS,
      payload: {
        result
      }
    }
  },
  executeSqlFail (err: IDavinciResponse<any>['header']) {
    return {
      type: ActionTypes.EXECUTE_SQL_FAILURE,
      payload: {
        err
      }
    }
  },

  updateEditingView (view: IView) {
    return {
      type: ActionTypes.UPDATE_EDITING_VIEW,
      payload: {
        view
      }
    }
  },
  updateEditingViewInfo (viewInfo: IViewInfo) {
    return {
      type: ActionTypes.UPDATE_EDITING_VIEW_INFO,
      payload: {
        viewInfo
      }
    }
  },

  setSqlLimit (limit: number) {
    return {
      type: ActionTypes.SET_SQL_LIMIT,
      payload: {
        limit
      }
    }
  },

  resetViewState () {
    return {
      type: ActionTypes.RESET_VIEW_STATE,
      payload: {}
    }
  },

  /** Actions for fetch external authorization variables values */
  loadDacChannels () {
    return {
      type: ActionTypes.LOAD_DAC_CHANNELS,
      payload: {}
    }
  },
  dacChannelsLoaded (channels: IDacChannel[]) {
    return {
      type: ActionTypes.LOAD_DAC_CHANNELS_SUCCESS,
      payload: {
        channels
      }
    }
  },
  loadDacChannelsFail () {
    return {
      type: ActionTypes.LOAD_DAC_CHANNELS_FAILURE,
      payload: {}
    }
  },

  loadDacTenants (channelName: string) {
    return {
      type: ActionTypes.LOAD_DAC_TENANTS,
      payload: {
        channelName
      }
    }
  },
  dacTenantsLoaded (tenants: IDacTenant[]) {
    return {
      type: ActionTypes.LOAD_DAC_TENANTS_SUCCESS,
      payload: {
        tenants
      }
    }
  },
  loadDacTenantsFail () {
    return {
      type: ActionTypes.LOAD_DAC_TENANTS_FAILURE,
      payload: {}
    }
  },

  loadDacBizs (channelName: string, tenantId: number) {
    return {
      type: ActionTypes.LOAD_DAC_BIZS,
      payload: {
        channelName,
        tenantId
      }
    }
  },
  dacBizsLoaded (bizs: IDacBiz[]) {
    return {
      type: ActionTypes.LOAD_DAC_BIZS_SUCCESS,
      payload: {
        bizs
      }
    }
  },
  loadDacBizsFail () {
    return {
      type: ActionTypes.LOAD_DAC_BIZS_FAILURE,
      payload: {}
    }
  },
  /** */

  /** Actions for external usages */
  loadSelectOptions (controlKey: string, requestParams: { [viewId: string]: IDistinctValueReqeustParams }, itemId?: number) {
    return {
      type: ActionTypes.LOAD_SELECT_OPTIONS,
      payload: {
        controlKey,
        requestParams,
        itemId
      }
    }
  },
  selectOptionsLoaded (controlKey: string, values: any[], itemId?: number) {
    return {
      type: ActionTypes.LOAD_SELECT_OPTIONS_SUCCESS,
      payload: {
        controlKey,
        values,
        itemId
      }
    }
  },
  loadSelectOptionsFail (err) {
    return {
      type: ActionTypes.LOAD_SELECT_OPTIONS_FAILURE,
      payload: {
        err
      }
    }
  },

  loadViewData (id: number, requestParams: IDataRequestParams, resolve: (data: any[]) => void) {
    return {
      type: ActionTypes.LOAD_VIEW_DATA,
      payload: {
        id,
        requestParams,
        resolve
      }
    }
  },
  viewDataLoaded () {
    return {
      type: ActionTypes.LOAD_VIEW_DATA_SUCCESS
    }
  },
  loadViewDataFail (err) {
    return {
      type: ActionTypes.LOAD_VIEW_DATA_FAILURE,
      payload: {
        err
      }
    }
  },

  loadViewDistinctValue (viewId: number, params: IDistinctValueReqeustParams, resolve?: any) {
    return {
      type: ActionTypes.LOAD_VIEW_DISTINCT_VALUE,
      payload: {
        viewId,
        params,
        resolve
      }
    }
  },
  viewDistinctValueLoaded (data: any[]) {
    return {
      type: ActionTypes.LOAD_VIEW_DISTINCT_VALUE_SUCCESS,
      payload: {
        data
      }
    }
  },
  loadViewDistinctValueFail (err) {
    return {
      type: ActionTypes.LOAD_VIEW_DISTINCT_VALUE_FAILURE,
      payload: {
        err
      }
    }
  },

  loadViewDataFromVizItem (
    renderType: RenderType,
    itemId: number,
    viewId: number,
    requestParams: IDataRequestParams,
    vizType: 'dashboard' | 'display'
  ) {
    return {
      type: ActionTypes.LOAD_VIEW_DATA_FROM_VIZ_ITEM,
      payload: {
        renderType,
        itemId,
        viewId,
        requestParams,
        vizType
      }
    }
  },
  viewDataFromVizItemLoaded (
    renderType: RenderType,
    itemId: number,
    requestParams: IDataRequestParams,
    result: any[],
    vizType: 'dashboard' | 'display'
  ) {
    return {
      type: ActionTypes.LOAD_VIEW_DATA_FROM_VIZ_ITEM_SUCCESS,
      payload: {
        renderType,
        itemId,
        requestParams,
        result,
        vizType
      }
    }
  },
  loadViewDataFromVizItemFail (itemId: number, vizType: 'dashboard' | 'display') {
    return {
      type: ActionTypes.LOAD_VIEW_DATA_FROM_VIZ_ITEM_FAILURE,
      payload: {
        itemId,
        vizType
      }
    }
  }
  /** */
}
const mockAction = returnType(ViewActions)
export type ViewActionType = typeof mockAction

export default ViewActions
