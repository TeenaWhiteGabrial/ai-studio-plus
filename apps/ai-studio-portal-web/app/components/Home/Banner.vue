<template>
  <ClientOnly>
    <swiper-container
        ref="containerRef"
        :effect="effect"
        :pagination = "{
            el: '.swiper-pagination'
        }"
        :navigation="navigation"
        class="banner-swiper"
        :style="{
            height: height
        }"
        :autoplay="{
            delay: 4000,
            disableOnInteraction: false
        }"
    >
      <swiper-slide v-for="(item, idx) in contentList" :key="idx">
        <div class="banner-content">
            <div class="banner-media">
                <template v-if="isVideo(item.src)">
                    <video
                        :src="item.src"
                        class="banner-media-el"
                        autoplay
                        muted 
                        loop
                        playsinline
                    />
                </template>
                <template v-else>
                    <img
                        :src="item.src"
                        :alt="item.title"
                        class="banner-media-el"
                    />
                </template>
            </div>
            <div class="banner-info" :key="idx">
              <h2 class="text-shadow-xs text-12 font-bold mb-4 w-73">{{ item.title }}</h2>
              <p class="banner-desc">{{ item.desc }}</p>
              <template v-if="item.button?.show">
                <a
                  class="banner-btn"
                  :href="item.link"
                  target="_blank"
                  rel="noopener"
                  :style="{ backgroundImage: bgColor }"
                >
                  {{ item.button.text }}
                </a>
              </template>
            </div>
        </div>
      </swiper-slide>
    </swiper-container>
  </ClientOnly>
</template>

<script setup lang="ts">
    import type { SwiperEffect } from '@/types/swiper'

    interface buttonType {
        show: Boolean
        text: string
    }
    interface contentType {
        src: string
        title: string
        desc: string
        link: string
        button: buttonType
    }

    const { height, effect, navigation, buttonColor, contentList } = defineProps({
        height: {
            type: String,
            default: '30.625rem'
        },
        effect: {
            type: String as PropType<SwiperEffect>,
            default: 'fade'
        },
        navigation: {
            type: Boolean,
            default: false
        },
        buttonColor: {
            type: String,
            default: ''
        },
        contentList: {
            type: Array<contentType>,
            default: () => []
        },
    })

    const primaryTextColor = ref<string>('')
    const bgColor = ref<string>('')
    const { themeConfig } = useTheme()
    onMounted(async () => {
        primaryTextColor.value = themeConfig.value.primary
        bgColor.value = buttonColor || primaryTextColor.value
    })

</script>

<style scoped>
    .banner-swiper {
        width: 100%;
        /* height: 300px; */
        position: relative;
        background: #000;

    }

    .banner-content {
        width: 100%;
        height: 100%;
        position: relative;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .banner-media {
        width: 100%;
        height: 100%;
        position: absolute;
        left: 0;
        top: 0;
        z-index: 1;
        overflow: hidden;
    }

    .banner-media-el {
        width: 100%;
        height: 100%;
        object-fit: cover;
        display: block;
    }

    .banner-info {
        position: relative;
        z-index: 2;
        width: 100%;
        text-align: center;
        color: #000;
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        pointer-events: none;
    }


    .banner-desc {
        font-size: 1.2rem;
        margin-bottom: 1.5rem;
        text-shadow: 0 1px 6px rgba(0, 0, 0, 0.2);
    }

    .banner-btn {
        pointer-events: auto;
        display: inline-block;
        padding: 0.7em 2em;
        background: var(--color-primary);
        color: #fff;
        border-radius: 2em;
        font-size: 1rem;
        font-weight: 500;
        text-decoration: none;
        box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15);
        transition: background 0.2s;
    }

    .banner-btn:hover {
        background: var(--color-primary-dark);
    }

    .fade-up-enter-active,
    .fade-up-leave-active {
        transition: opacity 5s cubic-bezier(0.23, 1, 0.32, 1), transform 5s cubic-bezier(0.23, 1, 0.32, 1);
    }

    .fade-up-enter-from,
    .fade-up-leave-to {
        opacity: 0;
        transform: translateY(60px) scale(0.96);
        transition: all 5s;
    }

    .fade-up-enter-to,
    .fade-up-leave-from {
        opacity: 1;
        transform: translateY(0) scale(1);
        transition: all 5s;
    }
</style>