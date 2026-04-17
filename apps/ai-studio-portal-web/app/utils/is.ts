/**
 * 判断类工具函数集合
 * 提供各种类型检测、验证和判断的工具函数
 */

const toString = Object.prototype.toString

/**
 * 检测值的具体类型
 * @param val 要检测的值
 * @param type 期望的类型字符串
 * @returns 是否为指定类型
 */
export function is(val: any, type: string): boolean {
    return toString.call(val) === `[object ${type}]`
}

/**
 * 检测值是否已定义（非undefined）
 * @param val 要检测的值
 * @returns 是否已定义
 */
export function isDef(val: any): boolean {
    return typeof val !== 'undefined'
}

/**
 * 检测值是否为undefined
 * @param val 要检测的值
 * @returns 是否为undefined
 */
export function isUndef(val: any): boolean {
    return typeof val === 'undefined'
}

/**
 * 检测值是否为null
 * @param val 要检测的值
 * @returns 是否为null
 */
export function isNull(val: any): boolean {
    return val === null
}

/**
 * 检测值是否为空字符串
 * @param val 要检测的值
 * @returns 是否为空字符串
 */
export function isWhitespace(val: any): boolean {
    return val === ''
}

/**
 * 检测值是否为普通对象（非null的对象）
 * @param val 要检测的值
 * @returns 是否为普通对象
 */
export function isObject(val: any): boolean {
    return !isNull(val) && is(val, 'Object')
}

/**
 * 检测值是否为数组
 * @param val 要检测的值
 * @returns 是否为数组
 */
export function isArray(val: any): boolean {
    return val && Array.isArray(val)
}

/**
 * 检测值是否为字符串
 * @param val 要检测的值
 * @returns 是否为字符串
 */
export function isString(val: any): boolean {
    return is(val, 'String')
}

/**
 * 检测值是否为数字
 * @param val 要检测的值
 * @returns 是否为数字
 */
export function isNumber(val: any): boolean {
    return is(val, 'Number')
}

/**
 * 检测值是否为布尔值
 * @param val 要检测的值
 * @returns 是否为布尔值
 */
export function isBoolean(val: any): boolean {
    return is(val, 'Boolean')
}

/**
 * 检测值是否为日期对象
 * @param val 要检测的值
 * @returns 是否为日期对象
 */
export function isDate(val: any): boolean {
    return is(val, 'Date')
}

/**
 * 检测值是否为正则表达式
 * @param val 要检测的值
 * @returns 是否为正则表达式
 */
export function isRegExp(val: any): boolean {
    return is(val, 'RegExp')
}

/**
 * 检测值是否为函数
 * @param val 要检测的值
 * @returns 是否为函数
 */
export function isFunction(val: any): boolean {
    return typeof val === 'function'
}

/**
 * 检测值是否为Promise对象
 * @param val 要检测的值
 * @returns 是否为Promise对象
 */
export function isPromise(val: any): boolean {
    return is(val, 'Promise') && isObject(val) && isFunction(val.then) && isFunction(val.catch)
}

/**
 * 检测值是否为DOM元素
 * @param val 要检测的值
 * @returns 是否为DOM元素
 */
export function isElement(val: any): boolean {
    return isObject(val) && !!val.tagName
}

/**
 * 检测值是否为Window对象
 * @param val 要检测的值
 * @returns 是否为Window对象
 */
export function isWindow(val: any): boolean {
    return typeof window !== 'undefined' && isDef(window) && is(val, 'Window')
}

/**
 * 检测值是否为null或undefined
 * @param val 要检测的值
 * @returns 是否为null或undefined
 */
export function isNullOrUndef(val: any): boolean {
    return isNull(val) || isUndef(val)
}

/**
 * 检测值是否为null、undefined或空字符串
 * @param val 要检测的值
 * @returns 是否为null、undefined或空字符串
 */
export function isNullOrWhitespace(val: any): boolean {
    return isNullOrUndef(val) || isWhitespace(val)
}

/**
 * 检测值是否为空（空数组、空字符串、空对象、空Map、空Set）
 * @param val 要检测的值
 * @returns 是否为空
 */
export function isEmpty(val: any): boolean {
    if (isArray(val) || isString(val)) {
        return val.length === 0
    }

    if (val instanceof Map || val instanceof Set) {
        return val.size === 0
    }

    if (isObject(val)) {
        return Object.keys(val).length === 0
    }

    return false
}

/**
 * 类似MySQL的IFNULL函数
 * 如果第一个参数为null、undefined或空字符串，则返回第二个参数作为备用值
 * @param val 要检测的值
 * @param def 备用值，默认为空字符串
 * @returns 如果val为空则返回def，否则返回val
 */
export function ifNull(val: any, def: number | boolean | string = '') {
    return isNullOrWhitespace(val) ? def : val
}


/**
 * 检测字符串是否为外部URL链接
 * 支持http:、https:、等协议
 * @param path 要检测的字符串
 * @returns 是否为外部链接
 */
export function isExternalUrl(path: string): boolean {
    const reg = /^https?:\/\/[-\w+&@#/%?=~|!:,.;]+[-\w+&@#/%=~|]$/
    return reg.test(path)
}

/**
 * 检测是否为视频链接
 */
export function isVideo(src: string) {
    return /\.(mp4|webm|ogg)$/i.test(src)
}

/**
 * 检测当前环境是否为服务端
 * 通过检查window对象是否存在来判断
 */
export const isServer = typeof window === 'undefined'

/**
 * 检测当前环境是否为客户端
 * 与服务端检测相反
 */
export const isClient = !isServer

/** 检测是否为正确邮箱格式 */
export function isvalidateEmail(email: string): boolean {
    const emailRegex = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/
    return emailRegex.test(email)
}

/** 验证手机号格式 */
export function isValidatePhone(phone: string): boolean {
    const phoneRegex = /^1[3-9]\d{9}$/
    return phoneRegex.test(phone)
}

/** 检测是否为正确身份证号格式 */
export function isValidateIdNumber(idNumber: string): boolean {
    // 1. 基础格式正则检查（保留原正则，但后续需验证日期真实性）
    const idRegex = /^[1-9]\d{5}(18|19|20)\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\d{3}[0-9Xx]$/;
    if (!idRegex.test(idNumber)) return false;

    // 2. 提取年月日并验证真实性
    const year = parseInt(idNumber.slice(6, 10), 10);
    const month = parseInt(idNumber.slice(10, 12), 10) - 1; // Date对象月份从0开始
    const day = parseInt(idNumber.slice(12, 14), 10);

    // 创建Date对象并验证（非法日期会自动调整，如2月30日→3月2日）
    const date = new Date(year, month, day);
    return (
        date.getFullYear() === year &&
        date.getMonth() === month &&
        date.getDate() === day
    );
}