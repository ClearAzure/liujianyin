<template>
  <div class="page-detail" v-if="album">
    <div class="album-header">
      <div class="album-cover" @click="pickCover" :title="uploading ? '上传中…' : '更换封面'">
        <img v-if="coverUrl" :src="coverUrl" alt="cover" />
        <Icon v-else icon="mdi:album" />
        <div class="cover-mask">
          <Icon v-if="uploading" icon="mdi:loading" class="spin" />
          <template v-else>
            <Icon icon="mdi:camera-plus" /> 更换封面
          </template>
        </div>
      </div>
      <input ref="fileInput" type="file" accept="image/*" hidden @change="onCoverChange" />
      <div class="album-info">
        <h2>{{ album.name }}</h2>
        <p class="meta">
          歌手：
          <router-link v-if="album.artistId" :to="`/artist/${album.artistId}`" class="link">{{ album.artistName
            }}</router-link>
          <template v-else>{{ album.artistName }}</template>
        </p>
        <p v-if="album.publishTime" class="meta">发布时间：{{ album.publishTime }}</p>
        <p class="meta">{{ album.musicCount || 0 }} 首歌曲</p>
      </div>
    </div>
    <MusicList :songs="album.songs || []" @deleted="load" />
    <!-- 监听 MusicList 发出来的 deleted 事件，一旦发生，就执行 load()。 -->
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import MusicList from '../components/MusicList.vue'
import * as albumAPI from '../api/album'
import * as fileAPI from '../api/file'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const album = ref(null)
const fileInput = ref(null)
const uploading = ref(false)

// 封面：优先专辑自定义封面，否则用第一首歌的封面
const coverUrl = computed(() => {
  if (album.value?.coverUrl) return album.value.coverUrl
  const songs = album.value?.songs || []
  return songs.length ? songs[0].coverUrl : ''
})

async function load() {
  try {
    album.value = await albumAPI.getDetail(route.params.id)
  } catch {
    album.value = null
  }
}
watch(() => route.params.id, load, { immediate: true })//监视路由参数id变化,变化就调用load函数，並且一开始立即就执行一次load函数

function pickCover() {
  if (!userStore.isLogin) { userStore.openLogin(); return }
  if (!userStore.isAdmin) return
  fileInput.value?.click()
}

async function onCoverChange(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  try {
    const fd = new FormData()
    fd.append('file', file)
    const data = await fileAPI.uploadImage(fd)//先上传要更换的封面图片，返回图片的url
    await albumAPI.update(album.value.id, { coverUrl: data.url })//再把这个url更新到专辑的coverUrl字段.才算真正更换了封面
    await load()//刷新整个页面
    ElMessage.success('封面已更新')
  } catch (err) {
    ElMessage.error(err.message || '封面更新失败')
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}
</script>

<style scoped>
.album-header {
  display: flex;
  align-items: flex-end;
  gap: 28px;
  margin-bottom: 24px;
}

.album-cover {
  position: relative;
  cursor: pointer;
  width: 160px;
  height: 160px;
  border-radius: 12px;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72px;
  color: var(--text-muted);
}

.album-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-mask {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  background: rgba(0, 0, 0, 0.5);
  color: #fff;
  font-size: 13px;
  opacity: 0;
  transition: opacity 0.2s;
}

.album-cover:hover .cover-mask {
  opacity: 1;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.album-info h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.album-info .meta {
  margin: 0 0 6px;
  color: var(--text-muted);
  font-size: 14px;
}

.album-info .link {
  color: var(--text-secondary);
  text-decoration: none;
}

.album-info .link:hover {
  color: var(--accent);
}
</style>
