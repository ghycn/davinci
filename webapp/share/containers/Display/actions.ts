

import { ActionTypes } from './constants'

export function loadDisplay (token, resolve, reject) {
  return {
    type: ActionTypes.LOAD_SHARE_DISPLAY,
    payload: {
      token,
      resolve,
      reject
    }
  }
}
export function displayLoaded (display, slide, widgets) {
  return {
    type: ActionTypes.LOAD_SHARE_DISPLAY_SUCCESS,
    payload: {
      display,
      slide,
      widgets
    }
  }
}
export function loadDisplayFail (error) {
  return {
    type: ActionTypes.LOAD_SHARE_DISPLAY_FAILURE,
    payload: {
      error
    }
  }
}

export function loadLayerData (renderType, layerId, dataToken, requestParams) {
  return {
    type: ActionTypes.LOAD_LAYER_DATA,
    payload: {
      renderType,
      layerId,
      dataToken,
      requestParams
    }
  }
}
export function layerDataLoaded (renderType, layerId, data) {
  return {
    type: ActionTypes.LOAD_LAYER_DATA_SUCCESS,
    payload: {
      renderType,
      layerId,
      data
    }
  }
}
export function loadLayerDataFail (error) {
  return {
    type: ActionTypes.LOAD_LAYER_DATA_FAILURE,
    payload: {
      error
    }
  }
}

