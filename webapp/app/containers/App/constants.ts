

import { DownloadStatus } from './types'

export const LOGIN = 'zhihui/App/LOGIN'
export const LOGGED = 'zhihui/App/LOGGED'
export const LOGIN_ERROR = 'zhihui/App/LOGIN_ERROR'
export const ACTIVE = 'zhihui/App/ACTIVE'
export const ACTIVE_SUCCESS = 'zhihui/App/ACTIVE_SUCCESS'
export const ACTIVE_ERROR = 'zhihui/App/ACTIVE_ERROR'

export const JOIN_ORGANIZATION = 'zhihui/App/JOIN_ORGANIZATION'
export const JOIN_ORGANIZATION_SUCCESS = 'zhihui/App/JOIN_ORGANIZATION_SUCCESS'
export const JOIN_ORGANIZATION_ERROR = 'zhihui/App/JOIN_ORGANIZATION_ERROR'

export const LOGOUT = 'zhihui/App/LOGOUT'
export const SET_LOGIN_USER = 'zhihui/App/SET_LOGIN_USER'
export const GET_LOGIN_USER = 'zhihui/App/GET_LOGIN_USER'
export const GET_LOGIN_USER_ERROR = 'zhihui/App/GET_LOGIN_USER_ERROR'
export const SHOW_NAVIGATOR = 'zhihui/App/SHOW_NAVIGATOR'
export const HIDE_NAVIGATOR = 'zhihui/App/HIDE_NAVIGATOR'
export const CHECK_NAME = 'zhihui/App/CHECK_NAME'

export const UPDATE_PROFILE = 'zhihui/App/UPDATE_PROFILE'
export const UPDATE_PROFILE_SUCCESS = 'zhihui/App/UPDATE_PROFILE_SUCCESS'
export const UPDATE_PROFILE_ERROR = 'zhihui/App/UPDATE_PROFILE_ERROR'

export const UPLOAD_AVATAR_SUCCESS = 'zhihui/App/UPLOAD_AVATAR_SUCCESS'

export const CHANGE_USER_PASSWORD = 'zhihui/User/CHANGE_USER_PASSWORD'
export const CHANGE_USER_PASSWORD_SUCCESS = 'zhihui/User/CHANGE_USER_PASSWORD_SUCCESS'
export const CHANGE_USER_PASSWORD_FAILURE = 'zhihui/User/CHANGE_USER_PASSWORD_FAILURE'

export const LOAD_DOWNLOAD_LIST = 'zhihui/Download/LOAD_DOWNLOAD_LIST'
export const LOAD_DOWNLOAD_LIST_SUCCESS = 'zhihui/Download/LOAD_DOWNLOAD_LIST_SUCCESS'
export const LOAD_DOWNLOAD_LIST_FAILURE = 'zhihui/Download/LOAD_DOWNLOAD_LIST_FAILURE'
export const DOWNLOAD_FILE = 'zhihui/Download/DOWNLOAD_FILE'
export const DOWNLOAD_FILE_SUCCESS = 'zhihui/Download/DOWNLOAD_FILE_SUCCESS'
export const DOWNLOAD_FILE_FAILURE = 'zhihui/Download/DOWNLOAD_FILE_FAILURE'
export const CHANGE_DOWNLOAD_STATUS = 'zhihui/Download/CHANGE_DOWNLOAD_STATUS'
export const CHANGE_DOWNLOAD_STATUS_SUCCESS = 'zhihui/Download/CHANGE_DOWNLOAD_STATUS_SUCCESS'
export const CHANGE_DOWNLOAD_STATUS_FAILURE = 'zhihui/Download/CHANGE_DOWNLOAD_STATUS_FAILURE'
export const INITIATE_DOWNLOAD_TASK = 'zhihui/Download/INITIATE_DOWNLOAD_TASK'
export const INITIATE_DOWNLOAD_TASK_SUCCESS = 'zhihui/Download/INITIATE_DOWNLOAD_TASK_SUCCESS'
export const INITIATE_DOWNLOAD_TASK_FAILURE = 'zhihui/Download/INITIATE_DOWNLOAD_TASK_FAILURE'

export const CREATE_ORGANIZATION_PROJECT = 'zhihui/permission/CREATE_ORGANIZATION_PROJECT'
export const DELETE_ORGANIZATION_PROJECT = 'zhihui/permission/DELETE_ORGANIZATION_PROJECT'
export const INVITE_ORGANIZATION_MEMBER = 'zhihui/permission/CREATE_ORGANIZATION_PROJECT'
export const CHANGE_ORGANIZATION_MEMBER_ROLE = 'zhihui/permission/CHANGE_ORGANIZATION_MEMBER_ROLE'
export const DELETE_ORGANIZATION_MEMBER = 'zhihui/permission/DELETE_ORGANIZATION_MEMBER'
export const CREATE_ORGANIZATION_TEAM = 'zhihui/permission/CREATE_ORGANIZATION_TEAM'
export const UPDATE_ORGANIZATION = 'zhihui/permission/UPDATE_ORGANIZATION'
export const UPDATE_PROJECT_VISIBILITY = 'zhihui/permission/UPDATE_PROJECT_VISIBILITY'
export const DELETE_ORGANIZATION = 'zhihui/permission/DELETE_ORGANIZATION'
export const TRANSFER_PROJECT_TO_ORGANIZATION = 'zhihui/permission/TRANSFER_PROJECT_TO_ORGANIZATION'
export const ADD_TEAM_MEMBER = 'zhihui/permission/ADD_TEAM_MEMBER'
export const CHANGE_TEAM_MEMBER_ROLE = 'zhihui/permission/CHANGE_TEAM_MEMBER_ROLE'
export const DELETE_TEAM_MEMBER = 'zhihui/permission/DELETE_TEAM_MEMBER'
export const ADD_TEAM_PROJECT = 'zhihui/permission/ADD_TEAM_PROJECT'
export const DELETE_TEAM_PROJECT = 'zhihui/permission/DELETE_TEAM_PROJECT'
export const UPDATE_TEAM_PROJECT_PERMISSION = 'zhihui/permission/UPDATE_TEAM_PROJECT_PERMISSION'
export const UPDATE_TEAM = 'zhihui/permission/UPDATE_TEAM'
export const DELETE_TEAM = 'zhihui/permission/DELETE_TEAM'

export const DOWNLOAD_STATUS_COLORS = {
  [DownloadStatus.Processing]: 'blue',
  [DownloadStatus.Success]: 'green',
  [DownloadStatus.Failed]: 'red',
  [DownloadStatus.Downloaded]: 'grey'
}

export const DOWNLOAD_STATUS_LOCALE = {
  [DownloadStatus.Processing]: '处理中',
  [DownloadStatus.Success]: '成功',
  [DownloadStatus.Failed]: '失败',
  [DownloadStatus.Downloaded]: '已下载'
}
