

import {
  LOAD_SHARE_DASHBOARD,
  LOAD_SHARE_DASHBOARD_SUCCESS,
  LOAD_SHARE_DASHBOARD_FAILURE,
  LOAD_SHARE_WIDGET,
  LOAD_SHARE_WIDGET_SUCCESS,
  LOAD_SHARE_RESULTSET,
  LOAD_SHARE_RESULTSET_SUCCESS,
  SET_INDIVIDUAL_DASHBOARD,
  LOAD_WIDGET_CSV,
  LOAD_WIDGET_CSV_SUCCESS,
  LOAD_WIDGET_CSV_FAILURE,
  LOAD_SELECT_OPTIONS,
  LOAD_SELECT_OPTIONS_SUCCESS,
  LOAD_SELECT_OPTIONS_FAILURE,
  RESIZE_ALL_DASHBOARDITEM,
  DRILL_DASHBOARDITEM,
  DELETE_DRILL_HISTORY,
  SET_SELECT_OPTIONS,
  SELECT_DASHBOARD_ITEM_CHART,
  GLOBAL_CONTROL_CHANGE
} from './constants'

export function getDashboard (token, reject) {
  return {
    type: LOAD_SHARE_DASHBOARD,
    payload: {
      token,
      reject
    }
  }
}

export function dashboardGetted (dashboard) {
  return {
    type: LOAD_SHARE_DASHBOARD_SUCCESS,
    payload: {
      dashboard
    }
  }
}

export function loadDashboardFail () {
  return {
    type: LOAD_SHARE_DASHBOARD_FAILURE
  }
}

export function getWidget (token, resolve, reject) {
  return {
    type: LOAD_SHARE_WIDGET,
    payload: {
      token,
      resolve,
      reject
    }
  }
}

export function widgetGetted (widget) {
  return {
    type: LOAD_SHARE_WIDGET_SUCCESS,
    payload: {
      widget
    }
  }
}

export function getResultset (renderType, itemId, dataToken, requestParams) {
  return {
    type: LOAD_SHARE_RESULTSET,
    payload: {
      renderType,
      itemId,
      dataToken,
      requestParams
    }
  }
}

export function resultsetGetted (renderType, itemId, requestParams, resultset) {
  return {
    type: LOAD_SHARE_RESULTSET_SUCCESS,
    payload: {
      renderType,
      itemId,
      requestParams,
      resultset
    }
  }
}

export function setIndividualDashboard (widgetId, token) {
  return {
    type: SET_INDIVIDUAL_DASHBOARD,
    payload: {
      widgetId,
      token
    }
  }
}

export function loadWidgetCsv (itemId, requestParams, token) {
  return {
    type: LOAD_WIDGET_CSV,
    payload: {
      itemId,
      requestParams,
      token
    }
  }
}

export function widgetCsvLoaded (itemId) {
  return {
    type: LOAD_WIDGET_CSV_SUCCESS,
    payload: {
      itemId
    }
  }
}

export function loadWidgetCsvFail (itemId) {
  return {
    type: LOAD_WIDGET_CSV_FAILURE,
    payload: {
      itemId
    }
  }
}

export function loadSelectOptions (controlKey, dataToken, requestParams, itemId) {
  return {
    type: LOAD_SELECT_OPTIONS,
    payload: {
      controlKey,
      dataToken,
      requestParams,
      itemId
    }
  }
}

export function selectOptionsLoaded (controlKey, values, itemId) {
  return {
    type: LOAD_SELECT_OPTIONS_SUCCESS,
    payload: {
      controlKey,
      values,
      itemId
    }
  }
}

export function loadSelectOptionsFail (error) {
  return {
    type: LOAD_SELECT_OPTIONS_FAILURE,
    payload: {
      error
    }
  }
}

export function resizeAllDashboardItem () {
  return {
    type: RESIZE_ALL_DASHBOARDITEM
  }
}

export function drillDashboardItem (itemId, drillHistory) {
  return {
    type: DRILL_DASHBOARDITEM,
    payload: {
      itemId,
      drillHistory
    }
  }
}

export function deleteDrillHistory (itemId, index) {
  return {
    type: DELETE_DRILL_HISTORY,
    payload: {
      itemId,
      index
    }
  }
}

export function setSelectOptions (controlKey, options, itemId) {
  return {
    type: SET_SELECT_OPTIONS,
    payload: {
      controlKey,
      options,
      itemId
    }
  }
}


export function selectDashboardItemChart (itemId, renderType, selectedItems) {
  return {
    type: SELECT_DASHBOARD_ITEM_CHART,
    payload: {
      itemId,
      renderType,
      selectedItems
    }
  }
}

export function globalControlChange (controlRequestParamsByItem) {
  return {
    type: GLOBAL_CONTROL_CHANGE,
    payload: {
      controlRequestParamsByItem
    }
  }
}
