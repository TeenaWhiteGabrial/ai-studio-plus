/*  API 响应数据结构 */
export interface ApiResponse<T> {
    /* 响应码
        200: 请求成功
        400: 业务逻辑限制不允许进行该项操作
        401: TOKEN 认证失败
        500: 服务器错误
        403: 后端有声明，但从未使用过
        404: 后端有声明，但从未使用过
    */
    code: 200 | 400 | 401 | 500 | 403 | 404
    /* 响应数据 */
    data: T
    /* 响应消息 */
    message?: string
    /* 响应消息 */
    msg?: string
}