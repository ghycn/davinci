

export default function () {
  const token = localStorage.getItem('TOKEN')
  if (token) {
    const expire = localStorage.getItem('TOKEN_EXPIRE')
    const timestamp = new Date().getTime()

    if (Number(expire) > timestamp) {
      return true
    } else {
      localStorage.removeItem('TOKEN')
      localStorage.removeItem('TOKEN_EXPIRE')
      return false
    }
  } else {
    return false
  }
}
