

import {
  LOGIN,
  LOGGED,
  LOGIN_ERROR,
  LOGOUT,
  SET_LOGIN_USER,
  SHOW_NAVIGATOR,
  HIDE_NAVIGATOR,
  ACTIVE_SUCCESS,
  UPLOAD_AVATAR_SUCCESS,
  LOAD_DOWNLOAD_LIST,
  LOAD_DOWNLOAD_LIST_SUCCESS,
  LOAD_DOWNLOAD_LIST_FAILURE,
  CHANGE_DOWNLOAD_STATUS_SUCCESS
} from './constants'
import { fromJS } from 'immutable'


const initialState = fromJS({
  logged: false,
  loginUser: null,
  loginLoading: false,
  navigator: true,
  downloadListLoading: false,
  downloadList: null,
  downloadListInfo: null
})

function appReducer (state = initialState, action) {
  const { type, payload } = action
  const loginUser = state.get('loginUser')
  const downloadList = state.get('downloadList')
  switch (type) {
    case LOGIN:
      return state
        .set('loginLoading', true)
    case LOGGED:
      return state
        .set('loginLoading', false)
        .set('logged', true)
        .set('loginUser', payload.user)
    case LOGIN_ERROR:
      return state
        .set('loginLoading', false)
    case ACTIVE_SUCCESS:
      return state
        .set('logged', true)
        .set('loginUser', payload.user)
    case LOGOUT:
      return state
        .set('logged', false)
        .set('loginUser', null)
    case SET_LOGIN_USER:
      return state
        .set('loginUser', payload.user)
    case UPLOAD_AVATAR_SUCCESS:
      const newLoginUser = {...loginUser, ...{avatar: payload.path}}
      localStorage.setItem('loginUser', JSON.stringify(newLoginUser))
      return state
        .set('loginUser', newLoginUser)
    case SHOW_NAVIGATOR:
      return state.set('navigator', true)
    case HIDE_NAVIGATOR:
      return state.set('navigator', false)
    case LOAD_DOWNLOAD_LIST:
      return state.set('downloadListLoading', true)
    case LOAD_DOWNLOAD_LIST_SUCCESS:
      return state
        .set('downloadListLoading', false)
        .set('downloadList', payload.list)
        .set('downloadListInfo', payload.list.reduce((info, item) => {
          info[item.id] = {
            loading: false
          }
          return info
        }, {}))
    case LOAD_DOWNLOAD_LIST_FAILURE:
      return state.set('downloadListLoading', false)
    case CHANGE_DOWNLOAD_STATUS_SUCCESS:
      return state.set('downloadList', downloadList.map((item) => {
        return item.id === payload.id
          ? { ...item, status: 3 }
          : item
      }))
    default:
      return state
  }
}

export default appReducer
