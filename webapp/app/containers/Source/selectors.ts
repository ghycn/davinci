

import { createSelector } from 'reselect'
import { SourceStateType } from './reducer'

const selectSource = (state) => state.get('source')

const makeSelectSources = () => createSelector(
  selectSource,
  (sourceState: SourceStateType) => sourceState.get('sources')
)

const makeSelectListLoading = () => createSelector(
  selectSource,
  (sourceState: SourceStateType) => sourceState.get('listLoading')
)

const makeSelectFormLoading = () => createSelector(
  selectSource,
  (sourceState: SourceStateType) => sourceState.get('formLoading')
)

const makeSelectTestLoading = () => createSelector(
  selectSource,
  (sourceState: SourceStateType) => sourceState.get('testLoading')
)

export {
  selectSource,
  makeSelectSources,
  makeSelectListLoading,
  makeSelectFormLoading,
  makeSelectTestLoading
}
