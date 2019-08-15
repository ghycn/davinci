

import * as React from 'react'
import Canvas from './Canvas'

const styles = require('./Background.less')

export function Background (props) {
  return (
    <div className={styles.container}>
      <Canvas />
      <img className={styles.logo} src={require('../../assets/images/logo_light.svg')} />
      {props.children}
    </div>
  )
}

export default Background
