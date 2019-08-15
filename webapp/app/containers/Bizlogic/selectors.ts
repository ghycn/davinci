

import { createSelector } from 'reselect'

const selectBizlogic = (state) => state.get('bizlogic')

const makeSelectBizlogics = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('bizlogics')
)

const makeSelectSqlValidateCode = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('sqlValidateCode')
)

const makeSelectSqlValidateMsg = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('sqlValidateMessage')
)

const makeSelectTableLoading = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('tableLoading')
)

const makeSelectModalLoading = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('modalLoading')
)

const makeSelectExecuteLoading = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('executeLoading')
)

const makeSelectViewTeam = () => createSelector(
  selectBizlogic,
  (bizlogicState) => bizlogicState.get('viewTeam')
)

export {
  selectBizlogic,
  makeSelectBizlogics,
  makeSelectSqlValidateMsg,
  makeSelectSqlValidateCode,
  makeSelectTableLoading,
  makeSelectModalLoading,
  makeSelectExecuteLoading,
  makeSelectViewTeam
}
