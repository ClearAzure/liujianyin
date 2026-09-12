<template>
  <div class="page-home">
    <div class="section">
      <h2>推荐歌曲</h2>
      <div class="card-grid">
        <MusicCard v-for="m in songs" :key="m.id" :music="m" />
      </div>
    </div>
    <div class="section">
      <h2>热门推荐</h2>
      <MusicList :songs="songs" @deleted="load" />
    </div>

    <!-- 分页条：卡片区与热门列表共用同一份数据，翻页同时生效 -->
    <div class="pager" v-if="totalPages > 1">
      <el-button size="small" :disabled="page <= 1" @click="go(page - 1)">上一页</el-button>
      <span class="page-info">{{ page }} / {{ totalPages }}</span>
      <el-button size="small" :disabled="page >= totalPages" @click="go(page + 1)">下一页</el-button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import MusicCard from '../components/MusicCard.vue'
import MusicList from '../components/MusicList.vue'
import * as musicAPI from '../api/music'

const PAGE_SIZE = 20
const songs = ref([])
const page = ref(1)
const total = ref(0)

const totalPages = computed(() => Math.max(1, Math.ceil(total.value / PAGE_SIZE)))

async function load() {
  try {
    const data = await musicAPI.hot(page.value, PAGE_SIZE)
    songs.value = data.list || []
    total.value = data.total || 0
  } catch {
    songs.value = []
    total.value = 0
  }
}

function go(p) {
  if (p < 1 || p > totalPages.value) return
  page.value = p
  load()
}

onMounted(load)
</script>

<style scoped>
.pager {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.page-info {
  color: var(--text-secondary);
  font-size: 14px;
}
</style>
