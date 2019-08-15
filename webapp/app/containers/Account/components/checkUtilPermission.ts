

export function initializePermission (currentProject, permissionItem) {
  let isShow
  if (currentProject && currentProject.permission) {
    const currentPermission = currentProject.permission[permissionItem]
    isShow = (currentPermission === 0 || currentPermission === 1) ? false : true
  } else {
    isShow = false
  }
  return isShow
}






