<template>
  <div class="page-detail" v-if="artist">
    <div class="artist-header">
      <div class="artist-avatar" @click="pickAvatar" :title="uploading ? '上传中…' : '更换头像'">
        <img v-if="artist.avatarUrl" :src="artist.avatarUrl" alt="avatar" />
        <Icon v-else icon="mdi:account-music" />
        <div class="avatar-mask">
          <Icon v-if="uploading" icon="mdi:loading" class="spin" />
          <template v-else>
            <Icon icon="mdi:camera-plus" /> 更换头像
          </template>
        </div>
      </div>
      <input ref="fileInput" type="file" accept="image/*" hidden @change="onAvatarChange" />
      <div class="artist-info">
        <h2>{{ artist.name }}</h2>
        <p v-if="artist.description" class="desc">{{ artist.description }}</p>
        <p class="meta">{{ artist.musicCount || 0 }} 首歌曲</p>
      </div>
    </div>
    <MusicList :songs="artist.songs || []" @deleted="load" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import MusicList from '../components/MusicList.vue'
import * as artistAPI from '../api/artist'
import * as fileAPI from '../api/file'
import { ElMessage } from 'element-plus'

const route = useRoute()
const userStore = useUserStore()
const artist = ref(null)
const fileInput = ref(null)
const uploading = ref(false)

async function load() {
  try {
    artist.value = await artistAPI.getDetail(route.params.id)
  } catch {
    artist.value = null
  }
}
watch(() => route.params.id, load, { immediate: true })

function pickAvatar() {
  if (!userStore.isLogin) { userStore.openLogin(); return }
  if (!userStore.isAdmin) return
  fileInput.value?.click()
}

async function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  uploading.value = true
  try {
    const fd = new FormData()//用于封装表单数据对象(键值对),尤其是文件
    fd.append('file', file)
    const data = await fileAPI.uploadImage(fd)
    await artistAPI.update(artist.value.id, { avatarUrl: data.url })
    await load()
    ElMessage.success('头像已更新')
  } catch (err) {
    ElMessage.error(err.message || '头像更新失败')
  } finally {
    uploading.value = false
    e.target.value = ''
  }
}
</script>

<style scoped>
.artist-header {
  display: flex;
  align-items: center;
  gap: 28px;
  margin-bottom: 24px;
}

.artist-avatar {
  position: relative;
  cursor: pointer;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  background: var(--bg-secondary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 72px;
  color: var(--text-muted);
}

.artist-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-mask {
  position: absolute;
  inset: 0;
  border-radius: 50%;
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

.artist-avatar:hover .avatar-mask {
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

.artist-info h2 {
  margin: 0 0 8px;
  font-size: 28px;
}

.artist-info .desc {
  margin: 0 0 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.artist-info .meta {
  margin: 0;
  color: var(--text-muted);
  font-size: 14px;
}
</style>
