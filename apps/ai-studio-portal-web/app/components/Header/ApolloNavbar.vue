<template>
  <header class="z-50 shadow-md w-full" :style="headerStyle">
    <nav class="mx-auto" :style="leftRightPaddingStyle">
      <div class="flex justify-between items-center" :style="heightStyle">
        <!-- Logo 区域 -->
        <div class="flex-shrink-0">
          <div @click="goToHome" class="flex items-center cursor-pointer"> 
            <img
              class="w-auto"
              :style="logoStyle"
              :src="logoUrl"
              alt="Logo"
            />
          </div>
        </div>

        <!-- 中间导航菜单 -->
        <div class="flex-1 hidden md:block" >
          <div  flex items-baseline space-x-8 h-full :style="columnStyle">
            <div
              v-for="item in navigationItems"
              :key="item.id"
              class="relative h-full group nav-item"
            >
                <div class="nav-font" :style="{ color: textColor }" @click="handleClickColumn(item)">                 
                  <span :class="{ 'nav-active-item': usePathMatch && currentMenuId === item.id }">{{ item.columnName }}</span>
                  <Icon class="transition-all duration-300 group-hover:rotate-180" v-if="item.child?.length > 0" name="material-symbols:keyboard-arrow-down" />
                </div>
                
                <div class="dropdown-menu hidden group-hover:block" :style="{ backgroundColor: columnBgColor, color: textColor }" v-if="item.child?.length > 0">
                  <div class="py-2 pr-4">
                    <div v-for="child in item.child" :key="child.id" class="dropdown-item" :class="{ relative: child.child && child.child?.length > 0 }" @click="handleClickColumn(child)">                  
                      <div>{{ child.columnName }}</div>
                      <Icon v-if="child.child && child.child?.length > 0" name="i-material-symbols:arrow-circle-right" size="20" />
                      <Icon v-else name="material-symbols:line-end-arrow" size="20" />
                      <div class="third-level-menu" :style="{ backgroundColor: columnBgColor, color: textColor }">
                        <div class="py-2">
                          <div v-for="child2 in child.child" :key="child2.id" class="third-level-item" @click="handleClickColumn(child2)">
                            <div>{{ child2.columnName }}</div>
                            <Icon name="material-symbols:line-end-arrow" size="20" />
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
            </div>
          </div>
        </div>

        <!-- 右侧登录按钮 -->
        <div class="flex items-center ml-6">
          <div v-for="(item,index) in workBenchList" :key="index">
            <div v-if="judgeWorkBenchItemShow(item)" @click="autoNavigate(item.link,{
              needLogin:item.needLogin
            })" class="mr-10 cursor-pointer" :style="{ 'line-height': '40px' }">{{ item.name }}</div>
          </div>

          <div v-if="showLoginButton">
            <template v-if="!isLoggedIn">
              <button
                v-if="!showRegisterButton"
                @click="goLoginPage()"
                class="btn-primary-outline"
                :style="{ color: textColor }"
              >
                立即登录
              </button>
              <div v-else flex>
                <div @click="goLoginPage()" class="mr-10 cursor-pointer" :style="{ 'line-height': '40px' }">登录</div>
                <button @click="goRegisterPage" class="btn-primary">免费注册</button>
              </div>
            </template>                   
            <div v-else class="relative  group cursor-pointer" >
              <div
                class="flex items-center text-sm h-full"
                :style="`line-height:${height}`"
              >
                <!-- 头像显示 -->
                <div class="w-10 h-10 rounded-full overflow-hidden border-2 border-primary shadow-sm mr-2 flex items-center justify-center bg-gray-100">
                  <img
                    v-if="authStore.avatar"
                    :src="authStore.avatar"
                    alt="用户头像"
                    class="w-full h-full object-cover"
                  />
                  <Icon
                    v-else
                    name="material-symbols:person"
                    size="24"
                    class="text-gray-400"
                  />
                </div>
                <span class="text-neutral-700">欢迎回来，{{ authStore.userName }}</span>
                <Icon class="ml-3 transition-all duration-300 group-hover:rotate-180" name="material-symbols:keyboard-arrow-down" size="18"  />
              </div>
              
              <!-- 用户菜单下拉 -->
              <div
                class="origin-top-right absolute right-0 min-w-36 rounded-md shadow-lg bg-white ring-1 ring-black ring-opacity-5 group-hover:block hidden"
              >
                <div class="py-1 ">
                  <div v-for="(item, index) in dropdownList" :key="index" @click="autoNavigate(item.link)" class="card-hover-shadow border-b-[#f3f3f3] border-b-1 border-b-solid block w-full text-left px-4 py-2 text-sm text-neutral-700">
                    {{ item.name }}
                  </div>
                  <div
                    @click="goLogout"
                    class="card-hover-shadow block w-full text-left px-4 py-2 text-sm text-neutral-700"
                  >
                    退出登录
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  </header>
</template>

<script setup lang="ts">
  import type { CSSPosition, CSSJustifyContent } from '@/types/css';
  import type { NavigationItem, WorkBenchItem } from '~~/shared/types/resource'

  /**
   * 外部传入的配置项：
   */
  const { height, columnMargin, leftRightPadding, logoHeight, fixStyle, columnPosition, usePathMatch, useApi, apiPath, columnData, columnBgColor, 
    showLoginButton, showRegisterButton, workBenchList, dropdownList
   } = defineProps({

    /** 栏目整体高度 */
    height: {
      type: String,
      default: '4rem'
    },
    /** 栏目左右间距 */
    columnMargin: {
      type: String,
      default: '3rem'
    },
    /** 整体两侧内间距 */
    leftRightPadding: {
      type: String,
      default: '2rem'
    },
    /** logo高度 */
    logoHeight: {
      type: String,
      default: '30px'
    },
    /** 悬浮样式 */
    fixStyle: {
      type: String as PropType<CSSPosition>,
      default: 'sticky'
    },
    /** 栏目靠左显示 */
    columnPosition: {
      type: String as PropType<CSSJustifyContent>,
      default: 'left'
    },
    /**
     * 使用路径匹配激活路由
     */
    usePathMatch: {
      type: Boolean,
      default: false
    },
    /** 使用API请求数据 */
    useApi: {
      type: Boolean,
      default: false
    },
    /** 数据请求API */
    apiPath: {
      type: String,
      default: ''
    },
    /** 栏目静态数据 */
    columnData: {
      type: Array as PropType<NavigationItem[]>,
      default: () => []
    },
    /** 栏目背景色 */
    columnBgColor: {
      type: String,
      default: '#fff'
    },
    /** 文字颜色 */
    textColor: {
      type: String,
      default: '#333'
    },
    /** 登录方式，字符串枚举：hidden、maxkey、router */
    showLoginButton:{
      type:String,
      default: 'maxkey'
    },
    /** 显示注册按钮 */
    showRegisterButton:{
      type:Boolean,
      default:false
     },
     /** 登录按钮左边的跳转链接 */
     workBenchList:{
      type:Array as PropType<WorkBenchItem[]>,
      default: () => []
     },
     dropdownList:{
       type: Array as PropType<WorkBenchItem[]>,
       default:()=>[]
     }
  })

  function judgeWorkBenchItemShow(item:WorkBenchItem){
    if(!item.showRoles || item.showRoles.length === 0){
      return true
    }
    let result = false
    item.showRoles.forEach((role)=>{
      if(authStore.roles?.includes(role)){
        result = true
      }
    })
    return result
  }

  /**  位置样式  */
  const headerStyle = computed(() => ({
    top: fixStyle === 'sticky' ? '0' : 'auto',
    position: fixStyle,
    backgroundColor: columnBgColor
  }))

  // 整体高度样式
  const heightStyle = computed(() => ({
    height: height,
  }))

  // 整体两侧内间距样式
  const leftRightPaddingStyle = computed(() => ({
    paddingLeft: leftRightPadding,
    paddingRight: leftRightPadding,
  }))

  // LOGO高度样式
  const logoStyle = computed(() => ({
    height: logoHeight,
  }))

  // 栏目样式
  const columnStyle = computed(() => ({
    justifyContent: columnPosition,
    marginLeft: columnMargin,
    marginRight: columnMargin,
    lineHeight: height,
  }))

  // 使用网站配置
  const { siteConfig } = useSite()

  // 导航菜单数据
  const navigationItems = ref<NavigationItem[]>([])

  // 当前菜单路由
  const currentMenuId = ref<string | null>(null)

  const authStore = useAuthStore()

  const isLoggedIn = computed(() => {
    return !!authStore.token
  })

  const logoUrl = ref<string>('')
  onMounted(async () => {
    if (useApi) {
      const { code, data, messgae }: any = await useSimpleFetch<{ code: number; data: NavigationItem[]; message: string }>(apiPath)
      if (code === 200 && data) {
        navigationItems.value = data
      } else {
        console.error('获取导航菜单数据失败:', messgae || '未知错误')
      }
    } else {
      navigationItems.value = columnData || []
    }
    currentMenuId.value = findCurrentMenuId(navigationItems.value)
    logoUrl.value = siteConfig.logo
  })


  function findCurrentMenuId(items: NavigationItem[], parentId?: string): string | null {
    const currentRoute = useRoute().path

    for (const item of items) {
      if (item.columnLink === currentRoute) {
        return parentId || item.id
      } else if (item.child?.length > 0) {
        const found = findCurrentMenuId(item.child, item.id)
        if (found) {
          return found
        }
      }
    }

    return null
  }

  async function handleClickColumn(item: NavigationItem) {
    // 有子栏目，取消点击事件
    if (item.child?.length > 0) {
      return
    } else if (isExternalUrl(item.columnLink)) {
      // 判断是否是外链
      window.open(item.columnLink, item.runType === '1' ? '_blank' : '_self')
      return
    } else if (item.columnLink && item.columnLink.startsWith('/')) {
      // 判断是否是标准路由（以 / 开头）
      await navigateTo(item.columnLink)
      currentMenuId.value = item.id
      return
    } else {
      // 拼接跳转
      await navigateTo(`/${item.columnLink}`)
      currentMenuId.value = item.id
    }
  }
</script>

<style scoped>
  .nav-item {
    @apply after:content-[''] after:absolute after:bottom-2 after:left-1/2 after:-translate-x-1/2 after:h-0.75 after:w-0 after:bg-primary after:transition-all after:duration-300 hover:after:w-full
  }

  .nav-font {
    @apply group-hover:text-primary transition-all duration-300 flex items-center cursor-pointer gap-2 relative
  }

  .nav-active-item {
    @apply text-primary font-bold after:content-[''] after:absolute after:bottom-2 after:left-1/2 after:-translate-x-1/2 after:h-0.75 after:w-full after:bg-primary
  }

  .dropdown-menu {
    /* background: hsla(0, 0%, 100%, .97); */
    border-radius: 16px;
    box-shadow: 0 20px 60px #00000026, 0 8px 32px #4153ff1a, inset 0 1px #fffc;
    left: 50%;
    width: max-content;
    min-width: 200px;
    position: absolute;
    top: 100%;
    transform: translate(-50%);
    transition: all .5s ease;
    z-index: 10001;
  }

  .dropdown-item {
    border-radius: 1rem;
    cursor: pointer;
    display: flex;
    font-weight: 400;
    gap: 1rem;
    line-height: 2rem;
    margin: 0 0.5rem;
    padding: 1rem 2rem;
    position: relative;
    transition: all .3s cubic-bezier(.4, 0, .2, 1);
    width: 100%;
    box-sizing: border-box;
  }

  .dropdown-item>div,
  .third-level-item>div {
    flex: 1;
    white-space: nowrap;
    min-width: 0;
  }

  .dropdown-item>.iconify {
    margin-top: 0.625rem;
  }

  .third-level-menu {
    backdrop-filter: blur(20px);
    /* background: hsla(0, 0%, 100%, .98); */

    box-shadow: 0 20px 60px #00000026, 0 8px 32px #4153ff1a, inset 0 1px #fffc;
    border-radius: 16px;
    left: 100%;
    margin-left: 0.25rem;
    min-width: 220px;
    opacity: 0;
    pointer-events: none;
    position: absolute;
    top: 0;
    transform: translate(-10px);
    transition: all .3s ease;
    visibility: hidden;
    z-index: 10002;
  }

  .third-level-item {
    border-radius: 10px;
    cursor: pointer;
    display: flex;
    font-weight: 500;
    gap: 1rem;
    margin: 0 0.5rem;
    padding: 1rem 2rem;
    position: relative;
    transition: all .3s cubic-bezier(.4, 0, .2, 1);
  }

  .third-level-item>.iconify {
    margin-top: 0.625rem;

  }

  .dropdown-item:hover {
    background: linear-gradient(135deg, var(--color-primary-subtle), var(--color-primary-transparent));
    box-shadow: 0 4px 16px var(--color-primary-subtle), 0 0 0 1px var(--color-primary-transparent);
    color: var(--color-primary);
    transform: translate(0.25rem);
  }

  .dropdown-item:hover>.iconify,
  .third-level-item:hover>.iconify {
    color: var(--color-primary);
  }

  .dropdown-item:hover>.third-level-menu {
    opacity: 1;
    pointer-events: auto;
    visibility: visible;
  }

  .third-level-item:not(:last-child) {
    border-bottom: 1px solid rgba(59, 130, 246, .06);
    margin-bottom: 2px;
  }

  .third-level-item:hover {
    background: linear-gradient(135deg, var(--color-primary-subtle), var(--color-primary-transparent));
    box-shadow: 0 4px 12px var(--color-primary-subtle), 0 0 0 1px var(--color-primary-transparent);
    color: var(--color-primary);
  }

  .dropdown-item:not(:last-child) {
    border-bottom: 1px solid rgba(59, 130, 246, .08);
    margin-bottom: 0.25rem;
  }
</style>
