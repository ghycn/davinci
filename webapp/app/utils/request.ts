

import axios, { AxiosRequestConfig, AxiosResponse, AxiosPromise } from 'axios'

axios.defaults.validateStatus = function (status) {
  return status < 400
}

function parseJSON (response: AxiosResponse) {
  return response.data
}

function refreshToken (response: AxiosResponse) {
  const token = response.data.header && response.data.header.token
  if (token) {
    setToken(token)
    localStorage.setItem('TOKEN', token)
    localStorage.setItem('TOKEN_EXPIRE', `${new Date().getTime() + 3600000}`)
  }
  return response
}

export function request (config: AxiosRequestConfig): AxiosPromise
export function request (url: string, options?: AxiosRequestConfig): AxiosPromise
export default function request (url: any, options?: AxiosRequestConfig): AxiosPromise {
  return axios(url, options)
    .then(refreshToken)
    .then(parseJSON)
}

export function setToken (token: string) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

export function removeToken () {
  delete axios.defaults.headers.common['Authorization']
}

export function getToken () {
  return  axios.defaults.headers.common['Authorization']
}

interface IDavinciResponseHeader {
  code: number
  msg: string
  token: string
}

export interface IDavinciResponse<T> {
  header: IDavinciResponseHeader,
  payload: T
}
