const TokenKey = 'Admin-Token'

export function getCookieToken() {
    return useCookie(TokenKey).value || ''
}

export function setCookieToken(token:string) {
    useCookie(TokenKey).value = token
}

export function removeCookieToken() {
    useCookie(TokenKey).value = ''
}
