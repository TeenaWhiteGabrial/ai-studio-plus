<template>
  <div class="cloud-login-container">
    <!-- 登录表单卡片 -->
    <div class="login-card">
      <!-- 品牌标识 -->
      <div class="brand-section">
        <div class="cloud-icon">☁️</div>
        <h1 class="brand-title">云市场</h1>
        <p class="brand-subtitle">探索无限可能的云计算服务</p>
      </div>
      
      <el-form ref="loginRef" :model="loginForm" :rules="loginRules" class="login-form">
        <!-- 账号输入 -->
        <el-form-item prop="username" class="form-item">
          <el-input
            v-model="loginForm.username"
            type="text"
            size="large"
            auto-complete="off"
            placeholder="请输入账号"
            class="form-input"
          >
            <template #prefix>
              <Icon name="material-symbols:account-box-outline" size="20" />
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 密码输入 -->
        <el-form-item prop="password" class="form-item">
          <el-input
            v-model="loginForm.password"
            type="password"
            size="large"
            auto-complete="off"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
            class="form-input"
            show-password
          >
            <template #prefix>
              <Icon name="material-symbols:water-lock-rounded" size="20" />
            </template>
          </el-input>
        </el-form-item>
        
        <!-- 验证码输入 -->
        <el-form-item prop="code" class="form-item" v-if="captchaEnabled">
          <el-input
            v-model="loginForm.code"
            size="large"
            auto-complete="off"
            placeholder="请输入验证码"
            @keyup.enter="handleLogin"
            class="form-input code-input"
          >
            <template #prefix>
              <Icon name="material-symbols:verified-rounded" size="20" />
            </template>
          </el-input>
          <div class="captcha-container">
            <img :src="codeUrl" @click="getCode" class="captcha-image" alt="验证码" />
          </div>
        </el-form-item>
        
        <!-- 登录按钮 -->
        <el-form-item class="form-item submit-item">
          <el-button
            :loading="loading"
            size="large"
            type="primary"
            class="login-button"
            @click.prevent="handleLogin"
          >
            <span v-if="!loading">登 录</span>
            <span v-else>登 录 中...</span>
          </el-button>
        </el-form-item>
        <!-- 注册入口 -->
        <div class="text-3.5 color-[#666] text-center">
          还没有账号？<span class="color-[#0084ff] cursor-pointer" @click="goToRegister">立即注册</span>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
    import { encrypt, decrypt } from "@/utils/jsencrypt"
    definePageMeta({
        layout: 'empty'
    })
    interface CodeApiResponse {
        captchaEnabled: boolean
        code: number
        img: string
        uuid: string
        msg: string
    }
    // 响应式数据
    const router = useRouter()
    const route = useRoute()
    const authStore = useAuthStore()
    const loginRef = ref(null)

    // Cookie管理
    const usernameCookie = useCookie('username')
    const passwordCookie = useCookie('password')
    const rememberMeCookie = useCookie('rememberMe')

    // 表单数据
    const loginForm = ref({
        username: "",
        password: "",
        rememberMe: false,
        code: "",
        uuid: ""
    })

    // 表单验证规则
    const loginRules = ref({
        username: [
            { required: true, trigger: "blur", message: "请输入您的账号" },
            { min: 4, max: 20, message: "账号长度必须介于 4 和 20 之间", trigger: "blur" }
        ],
        password: [
            { required: true, trigger: "blur", message: "请输入您的密码" },
            { min: 6, max: 20, message: "密码长度必须介于 6 和 20 之间", trigger: "blur" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
    })

    // 验证码相关
    const codeUrl = ref("")
    const loading = ref(false)
    const captchaEnabled = ref(true)
    const redirect = ref('')

    // 监听路由变化获取重定向参数
    onMounted(async () => {
        if (route.query && route.query.redirect) {            
            redirect.value = route.query.redirect as string
        }
        if(!!authStore.token){
            redirectPage() 
        }
        await getCode()
        await getCookie()
    })

    /**
     * 处理登录逻辑
     */
    const handleLogin = async () => {
        if (!loginRef.value) return
        try {
            // @ts-ignore
            await loginRef.value.validate()
            loading.value = true

            // 记住密码逻辑
            if (loginForm.value.rememberMe) {
                usernameCookie.value = loginForm.value.username
                passwordCookie.value = await encrypt(loginForm.value.password)
                rememberMeCookie.value = String(loginForm.value.rememberMe)
            } else {
                usernameCookie.value = null
                passwordCookie.value = null
                rememberMeCookie.value = null
            }

            // 调用登录接口
            const loginResult = await authStore.ownLogin(loginForm.value.username, loginForm.value.password, loginForm.value.code, loginForm.value.uuid)
            if(loginResult.success){
                // 登录成功，处理重定向
                redirectPage()
                ElMessage.success('登录成功')
            } else {
                loading.value = false
                ElMessage.error(loginResult.message || '登录失败')
                throw new Error('登录失败')
            }
        } catch (error) {
            loading.value = false
            // 重新获取验证码
            if (captchaEnabled.value) {
                getCode()
            }
            console.error('登录失败:', error)
        }
    }

    // 处理重定向
    function redirectPage(){
        const query = route.query
        const otherQueryParams = Object.keys(query).reduce((acc, cur) => {
            if (cur !== "redirect") {
                // @ts-ignore
                acc[cur] = query[cur]
            }
            return acc
        }, {})
        // 使用window.location.href进行完全页面重新加载，以触发layout的onMounted生命周期
        const redirectPath = redirect.value || "/";
        const queryString = Object.keys(otherQueryParams).length > 0
            ? `?${new URLSearchParams(otherQueryParams as any).toString()}`
            : "";
        window.location.href = redirectPath + queryString;
    }
    /**
     * 获取验证码
     */
    async function getCode() {
        if(import.meta.client){
            const { code, captchaEnabled: captchaEnabledResponse, img, uuid, msg } = await useSimpleFetch<CodeApiResponse>('/prod-api/code')
            if (code === 200) {
                captchaEnabled.value = captchaEnabledResponse
                if (captchaEnabled.value) {
                    codeUrl.value = "data:image/gif;base64," + img
                    loginForm.value.uuid = uuid
                }
            } else {
                console.error('获取验证码失败:', msg || '未知错误')
            }
        }
    }

    /**
     * 从Cookie中恢复登录信息
     */
    const getCookie = async () => {
        const username = usernameCookie.value
        const password = passwordCookie.value
        const rememberMe = rememberMeCookie.value

        if (username) loginForm.value.username = username
        if (password) {
            try {
                loginForm.value.password = await decrypt(password)
            } catch (e) {
                console.error('解密密码失败:', e)
            }
        }
        if (rememberMe) loginForm.value.rememberMe = rememberMe === 'true'
    }

    /**
     * 跳转到注册页
     */
    const goToRegister = () => {
        router.push('/register')
    }
</script>

<style scoped>
    .cloud-login-container {        
        min-height: 100vh;
        padding-top:100px;
        padding-bottom:100px;
        width: 100%;
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        font-family: 'PingFang SC', 'Microsoft YaHei', 'Arial', sans-serif;
        background-image: url('https://uoc.oss.sd-jn-scyd-gyhlwzq-icp.inspurcloudoss.com/uoc/1TXyYkXW0Sjd1764764183072443.png');
        background-size: cover;
    }

    .cloud-pattern {
        position: absolute;
        width: 100%;
        height: 100%;
        background-image:
            radial-gradient(circle at 20% 10%, rgba(255, 255, 255, 0.1) 0%, transparent 20%),
            radial-gradient(circle at 80% 20%, rgba(255, 255, 255, 0.15) 0%, transparent 25%),
            radial-gradient(circle at 40% 70%, rgba(255, 255, 255, 0.12) 0%, transparent 22%),
            radial-gradient(circle at 70% 90%, rgba(255, 255, 255, 0.1) 0%, transparent 20%);
        background-size: 100% 100%;
        animation: cloudFloat 20s ease-in-out infinite;
    }

    @keyframes cloudFloat {
        0% {
            background-position: 0% 0%, 0% 0%, 0% 0%, 0% 0%;
        }

        25% {
            background-position: 5% 5%, -5% -5%, 10% 10%, -10% -10%;
        }

        50% {
            background-position: 10% 0%, 15% 15%, 5% 5%, 20% 20%;
        }

        75% {
            background-position: 5% 10%, -10% 5%, 15% -5%, 10% 25%;
        }

        100% {
            background-position: 0% 0%, 0% 0%, 0% 0%, 0% 0%;
        }
    }

    .gradient-overlay {
        position: absolute;
        width: 100%;
        height: 100%;
        background:
            linear-gradient(135deg, rgba(0, 82, 217, 0.9) 0%, rgba(0, 132, 255, 0.8) 100%),
            linear-gradient(120deg, rgba(66, 153, 225, 0.1) 0%, rgba(0, 82, 217, 0.2) 100%);
    }

    /* 登录卡片 */
    .login-card {
        position: relative;
        z-index: 1;
        width: 90%;
        max-width: 450px;
        background: rgba(255, 255, 255, 0.95);
        backdrop-filter: blur(10px);
        border-radius: 20px;
        padding: 40px;
        box-shadow:
            0 20px 40px rgba(0, 0, 0, 0.1),
            0 1px 3px rgba(0, 0, 0, 0.08),
            0 0 0 1px rgba(255, 255, 255, 0.1) inset;
        transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    /* 品牌部分 */
    .brand-section {
        text-align: center;
        margin-bottom: 40px;
    }

    .cloud-icon {
        font-size: 64px;
        margin-bottom: 16px;
        filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
    }

    .brand-title {
        font-size: 32px;
        font-weight: 700;
        color: #0052d9;
        margin-bottom: 8px;
        letter-spacing: 1px;
    }

    .brand-subtitle {
        font-size: 16px;
        color: #666;
        margin: 0;
    }

    /* 表单样式 */
    .login-form {
        width: 100%;
    }

    .form-item {
        margin-bottom: 24px;
        position: relative;
    }

    .form-input {
        height: 52px;
        font-size: 16px;
        border: 1px solid #e0e0e0;
        border-radius: 12px;
        transition: all 0.3s ease;
        background: rgba(255, 255, 255, 0.9);
    }

    .form-input:focus-within {
        border-color: #0084ff;
        box-shadow: 0 0 0 3px rgba(0, 132, 255, 0.1);
    }

    .form-input .el-input__inner {
        height: 100%;
        border: none;
        background: transparent;
        padding-left: 50px;
        font-size: 16px;
    }

    .form-input .el-input__inner:focus {
        box-shadow: none;
    }

    .input-icon {
        width: 24px;
        height: 24px;
        color: #999;
        transition: color 0.3s ease;
    }

    .form-input:focus-within .input-icon {
        color: #0084ff;
    }

    /* 验证码样式 */
    .code-input {
        width: calc(100% - 130px);
    }

    .captcha-container {
        position: absolute;
        right: 0;
        top: 50%;
        transform: translateY(-50%);
        width: 120px;
        height: 48px;
        border-radius: 12px;
        overflow: hidden;
    }

    .captcha-image {
        width: 100%;
        height: 100%;
        cursor: pointer;
        object-fit: cover;
        transition: transform 0.2s ease;
    }

    .captcha-image:hover {
        transform: scale(1.05);
    }

    /* 表单选项 */
    .form-options {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 32px;
    }

    .remember-checkbox {
        color: #666;
        font-size: 14px;
    }

    .forgot-password {
        color: #0084ff;
        font-size: 14px;
        text-decoration: none;
        transition: color 0.3s ease;
    }

    .forgot-password:hover {
        color: #0052d9;
        text-decoration: underline;
    }

    /* 登录按钮 */
    .submit-item {
        margin-bottom: 24px;
    }

    .login-button {
        width: 100%;
        height: 52px;
        font-size: 18px;
        font-weight: 600;
        background: linear-gradient(135deg, #0084ff 0%, #0052d9 100%);
        border: none;
        border-radius: 12px;
        transition: all 0.3s ease;
        box-shadow: 0 4px 12px rgba(0, 132, 255, 0.3);
    }

    .login-button:hover:not(:disabled) {
        background: linear-gradient(135deg, #0052d9 0%, #0084ff 100%);
        transform: translateY(-2px);
        box-shadow: 0 6px 16px rgba(0, 132, 255, 0.4);
    }

    .login-button:active:not(:disabled) {
        transform: translateY(0);
        box-shadow: 0 2px 8px rgba(0, 132, 255, 0.3);
    }

    /* 页脚 */
    .login-footer {
        position: relative;
        z-index: 1;
        margin-top: 40px;
        padding: 20px;
        text-align: center;
        color: rgba(255, 255, 255, 0.9);
        font-size: 14px;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
        .login-card {
            width: 95%;
            padding: 30px 25px;
            margin: 20px;
        }

        .brand-title {
            font-size: 28px;
        }

        .form-input {
            height: 48px;
        }

        .login-button {
            height: 48px;
            font-size: 16px;
        }

        .captcha-container {
            width: 100px;
            height: 44px;
        }

        .code-input {
            width: calc(100% - 110px);
        }
    }

    @media (max-width: 480px) {
        .login-card {
            padding: 25px 20px;
            border-radius: 16px;
        }

        .cloud-icon {
            font-size: 48px;
        }

        .brand-title {
            font-size: 24px;
        }

        .brand-subtitle {
            font-size: 14px;
        }

        .form-item {
            margin-bottom: 20px;
        }

        .form-input .el-input__inner {
            font-size: 14px;
        }
    }

    /* 加载动画 */
    @keyframes pulse {
        0% {
            opacity: 1;
        }

        50% {
            opacity: 0.6;
        }

        100% {
            opacity: 1;
        }
    }

    .login-button.is-loading .el-loading-spinner {
        margin-top: -15px;
    }
</style>