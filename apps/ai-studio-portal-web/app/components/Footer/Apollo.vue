<template>
  <footer class="footer">
    <div class="footer-content">
      <!-- 热门产品 -->
      <div class="footer-section">
        <h3>热门产品</h3>
        <div class="two-column-list">
          <!-- 第一列 -->
          <ul>
            <li v-for="(item, index) in recommendProductions?.slice(0, Math.ceil(recommendProductions.length / 2)) || []" :key="index"
                @click="navigateTo(item.link)">
                <a href="javascript:void(0);" class="text-3">{{ item.product_name }}</a>
            </li>
          </ul>
          <!-- 第二列 -->
          <ul>
            <li v-for="(item, index) in recommendProductions?.slice(Math.ceil(recommendProductions.length / 2)) || []" :key="index"
                @click="navigateTo(item.link)">
                <a href="javascript:void(0);" class="text-3">{{ item.product_name }}</a>
            </li>
          </ul>
        </div>
        <div class="mt-10 text-3 text-[#7c7c7c] w-full">
          <div>
            <span> 法律声明 | </span>
            <span> 隐私条款 | </span>
            <span> 网站地图 | </span>
            <span> {{ copyright }}</span>
          </div>
          <span> {{ recordInfo }}</span>
        </div>
      </div>

      <!-- 友情链接 -->
      <div class="footer-section border-r-gray border-r-0.25 border-r-solid">
        <h3>友情链接</h3>
        <div class="two-column-list">
          <!-- 第一列 -->
          <ul>
            <li v-for="(link, index) in friendlyLinks?.slice(0, Math.ceil(friendlyLinks.length / 2)) || []" :key="index">
              <a :href="link.url || '#'" target="_blank"  class="text-3">{{ link.name || '友情链接' }}</a>
            </li>
          </ul>
          <!-- 第二列 -->
          <ul>
            <li v-for="(link, index) in friendlyLinks?.slice(Math.ceil(friendlyLinks.length / 2)) || []" :key="index">
              <a :href="link.url || '#'" target="_blank"  class="text-3">{{ link.name || '友情链接' }}</a>
            </li>
          </ul>
        </div>
      </div>

      <!-- 联系咨询 -->
      <div class="mt-5 ml-5">
        <div>
          <Icon name="material-symbols:phone-in-talk-watchface-indicator" size="18" />
          <span ml-3 text-4>售前咨询热线</span>
        </div>
        <div ml-8 mt-1 text-6>{{ pre_sale_hotline }}</div>
        <div ml-8 mt-1 text-2.5 text-[#7c7c7c]>售后：{{ after_sale_hotline }}</div>
        <div flex mt-5>
          <Icon  name="material-symbols:mail-outline" size="18" />
          <div ml-2>{{ email }}</div>
        </div>
        <div mt-5 flex>
          <Icon name="material-symbols:mark-chat-read-outline" size="18" />
          <div ml-2 text-4>关注我们</div>
        </div>
        <img w-30 h-30 mt-4 ml-5 :src="contact_us[0]" />
      </div>
    </div>
  </footer>
</template>

<script lang="ts" setup>
interface RecommendProductions {
  product_name: string
  link: string
}

interface FriendlyLinks {
  name: string
  url: string
}


const {  recommendProductions, friendlyLinks, pre_sale_hotline, after_sale_hotline, email, contact_us } = defineProps({
  recommendProductions:{
    type: Array<RecommendProductions>,
    default:()=>[]
  },
  friendlyLinks:{
    type:Array<FriendlyLinks>,
    default:()=>[]
  },
  pre_sale_hotline:{
    type: String,
    default: ''
  },
  after_sale_hotline:{
    type: String,
    default: ''
  },
  email:{
    type: String,
    default: ''
  },
  contact_us:{
    type: Array<string>,
    default: ()=>[]
  },
  copyright:{
    type: String,
    default:''
  },
  recordInfo:{
    type: String,
    default:''
  }
})
</script>

<style scoped>
  .footer {
    background-color: #3c3c49;
    /* 深色背景 */
    color: #ffffff;
    padding: 40px 0 28px 0;
    font-family: 'Arial', sans-serif;
  }

  .footer-content {
    display: flex;
    flex-wrap: wrap;
    max-width: 1280px;
    margin: 0 auto;
  }

  .footer-section {
    margin: 20px;
    min-width: 20px;
  }

  .footer-section h3 {
    margin-bottom: 15px;
    position: relative;
    padding-bottom: 8px;
  }

  .footer-section ul {
    list-style: none;
    padding: 0;
  }

  /* 两列列表样式 */
  .two-column-list {
    display: flex;
    justify-content: space-between;
    gap: 20px;
  }

  .two-column-list ul {
    flex: 1;
    width: 180px;
  }

  /* 响应式调整 */
  @media (max-width: 768px) {
    .two-column-list {
      flex-direction: column;
      gap: 0;
    }

    .two-column-list ul:last-child {
      margin-top: 8px;
    }
  }

  .footer-section ul li {
    margin-bottom: 8px;
  }

  .footer-section ul li a {
    color: #fff;
    text-decoration: none;
    transition: color 0.3s;
  }

  .footer-section ul li a:hover {
    color: var(--color-primary);
  }

  .qr-code img {
    width: 120px;
    height: 120px;
    margin: 10px 0;
    border: 2px solid var(--color-primary);
    border-radius: 4px;
  }
</style>
