

/*
 *
 * LanguageProvider reducer
 *
 */

import { fromJS } from 'immutable'

import { CHANGE_LOCALE } from './constants'
import { DEFAULT_LOCALE } from '../../i18n'

const initialState = fromJS({
  locale: DEFAULT_LOCALE
})

function languageProviderReducer (state = initialState, action) {
  switch (action.type) {
    case CHANGE_LOCALE:
      return state.set('locale', action.locale)
    default:
      return state
  }
}

export default languageProviderReducer
