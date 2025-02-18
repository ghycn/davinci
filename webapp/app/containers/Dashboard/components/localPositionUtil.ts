

export function initializePosition (items) {
  return items.map((i) => ({
    x: i.x,
    y: i.y,
    w: i.width,
    h: i.height,
    i: `${i.id}`
  }))
}

export function changePosition (prev, current) {
  current.forEach((item, index) => {
    const prevItem = prev[index]
    prevItem.x = item.x
    prevItem.y = item.y
    if (prevItem.w !== item.w || prevItem.h !== item.h) {
      prevItem.w = item.w
      prevItem.h = item.h
    }
  })
  return prev
}

export function diffPosition (origin, current) {
  let sign = false
  for (let i = 0, cl = current.length; i < cl; i += 1) {
    const oItem = origin[i]
    const cItem = current[i]
    if (oItem.x !== cItem.x ||
        oItem.y !== cItem.y ||
        oItem.w !== cItem.w ||
        oItem.h !== cItem.h) {
      sign = true
      break
    }
  }
  return sign
}

// list 转成树形json
export function listToTree (list, parentId) {
  const ret = []
  for (const i in list) {
    if (list[i].parentId === parentId) {
      const leftList = list.filter((l) => l.id !== list[i].id)
      list[i].children = listToTree(leftList, list[i].id)
      ret.push(list[i])
    }
  }
  return ret
}

// 获取第一个dashboard的id
export function findFirstLeaf (tree) {
  if (tree.children.length === 0) {
    return -1
  }
  for (const child of tree.children) {
    if (child.type === 1) {
      return child.id
    } else {
      const leafId = findFirstLeaf(child)
      if (leafId > 0) {
        return leafId
      }
    }
  }
  return -1
}
