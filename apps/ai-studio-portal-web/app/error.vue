<script setup lang="ts">
  import type { NuxtError } from '#app'

  const { error } = defineProps({
    error: Object as () => NuxtError
  })
  console.log('报错信息咚咚锵：', error)
</script>

<template>
  <div>
    <HeaderApolloNavbar />
    <div class="min-h-screen flex flex-col justify-center items-center bg-gray-50">
      <div class="w-full flex justify-center" v-if="error?.statusCode === 404">
        <div class="flex items-center">
          <img class="w-100 h-100" src="~/assets/image/system/404BG.png" />
          <div class="mt-50">
            <h1 class="mb-10">{{ error?.cause ?? '啊哦，页面飞走了~' }}</h1>
              <ElButton size="large" type="primary" @click="goToHome">点此返回首页</ElButton>
          </div>
        </div>
      </div>
      <div v-else class="text-center p-10">
        <h1 class="text-2xl font-bold mb-4">发生错误</h1>
        <p class="text-gray-600 mb-6">{{ error?.message || '服务器错误，请稍后再试' }}</p>
          <ElButton type="primary" @click="goToHome">返回首页</ElButton>
      </div>
    </div>
  </div>
</template>
