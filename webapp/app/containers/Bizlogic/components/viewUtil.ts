

/*
* @params tree:要转换的树结构数据
* @output list:返回转换好的列表结构数据
* */
export function toListBF (tree) {
  const tempList = tree.slice(0)
  const res = []
  for (const node of tempList) {
      const { description, id, name, users } = node
      res.push({
          description,
          id,
          name,
          users,
          children: [],
          parentId: node.parentId === undefined ? null : node.parentId
      })
      if (node.children.length !== 0) {
          tempList.push(...node.children.map((item) => {
            item.parentId = node.id
            return item
          }))
      }
  }
  return res
}

/**
 * View Model Type:
 * 时间日期
 * 数值
 * 字符串
 * 地理-国家
 * 地理-省份
 * 地理-城市
 */
export const SQL_FIELD_TYPES = {
    date: ['DATE', 'DATETIME', 'TIMESTAMP', 'TIME', 'YEAR'],
    number: [
        'TINYINT', 'SMALLINT', 'MEDIUMINT', 'INT', 'INTEGER', 'BIGINT',
        'FLOAT', 'DOUBLE', 'DOUBLE PRECISION', 'REAL', 'DECIMAL',
        'BIT', 'SERIAL', 'BOOL', 'BOOLEAN', 'DEC', 'FIXED', 'NUMERIC'
    ],
    string: [
        'CHAR', 'VARCHAR', 'TINYTEXT', 'TEXT', 'MEDIUMTEXT', 'LONGTEXT',
        'JSON', 'LINESTRING', 'MULTILINESTRING',
        'TINYBLOB', 'MEDIUMBLOB', 'BLOB', 'LONGBLOB',
        'BINARY', 'VARBINARY', 'ENUM', 'SET'
    ],
    geoCountry: [],
    geoProvince: [],
    geoCity: []
}

import { SQL_NUMBER_TYPES } from '../../../globalConstants'
export function getColumns (columns) {
    columns.map((i) => {
        const { date } = SQL_FIELD_TYPES
        let iVisualType
        for (const item in SQL_FIELD_TYPES) {
          if (SQL_FIELD_TYPES.hasOwnProperty(item)) {
            if (SQL_FIELD_TYPES[item].indexOf(i.type) >= 0) {
              iVisualType = item
            }
          }
        }

        i.visualType = iVisualType || 'string'
        i.modelType = SQL_NUMBER_TYPES.indexOf(i.type) < 0 ? 'category' : 'value'
        i.sqlType = i.type
        return i
      })
    return columns
}



