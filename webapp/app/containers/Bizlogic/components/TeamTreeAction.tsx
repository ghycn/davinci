

import * as React from 'react'
import { Input } from 'antd'
const styles = require('../Bizlogic.less')
import { ITeamParams } from '../../Bizlogic'

interface ITeamTreeActionProps {
  depth: number
  currentItem: {
    id: number,
    name: string,
    checked: boolean,
    params: [ITeamParams]
  },
  teamParams: [ITeamParams]
  onTeamParamChange: (id: number, index: number) => any
}

export class TeamTreeAction extends React.PureComponent<ITeamTreeActionProps, {}> {
  public render () {
    const {
      depth,
      currentItem,
      teamParams,
      onTeamParamChange
    } = this.props

    const paramsInput = (teamParams.length === 1 && teamParams[0].k === '')
    ? ''
    : teamParams.map((tp, index) => {
        return (
          <Input
            defaultValue={(currentItem.params.length && currentItem.params[index]) ? currentItem.params[index].v : ''}
            className={styles.teamInput}
            key={index}
            onChange={onTeamParamChange(currentItem.id, index)}
            disabled={!currentItem.checked}
            placeholder={tp.k}
          />
          )
      })

    const titleWidth = `${-18 * depth}px`
    return (
      <div className={styles.teamTree}>
        <span className={styles.teamTreeTitle} title={currentItem.name}>{currentItem.name}</span>
        <span style={{ marginLeft: titleWidth }}>{paramsInput}</span>
      </div>
    )
  }
}

export default TeamTreeAction
