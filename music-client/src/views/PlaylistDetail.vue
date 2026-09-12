<template>
  <div class="page-playlist-detail" v-if="playlist">
    <!-- 歌单头部：封面 + 名称 + 编辑入口 -->
    <div class="pl-header">
      <div class="pl-cover" @click="openEdit" title="点击编辑歌单">
        <img v-if="coverUrl" :src="coverUrl" alt="cover" />
        <div v-else class="pl-cover-placeholder"><Icon icon="mdi:playlist-music" /></div>
        <div class="pl-cover-mask"><Icon icon="mdi:pencil" /> 编辑</div>
      </div>
      <div class="pl-info">
        <h2>{{ playlist.name }}</h2>
        <p class="pl-meta">{{ playlist.songs?.length || 0 }} 首歌曲</p>
        <p v-if="playlist.description" class="pl-desc">{{ playlist.description }}</p>
        <el-button type="danger" round @click="openEdit">
          <Icon icon="mdi:pencil" /> 编辑歌单
        </el-button>
      </div>
    </div>

    <MusicList :songs="playlist.songs || []" :playlist-id="playlist.id" @removed="load" @deleted="load" />

    <!-- 编辑歌单弹窗：改名 + 上传封面 -->
    <el-dialog v-model="editDialog" title="编辑歌单" width="400px" align-center>
      <div class="edit-body">
        <div class="edit-cover" @click="pickCover">
          <img v-if="coverPreview" :src="coverPreview" alt="cover" />
          <div v-else class="edit-cover-empty">
            <Icon icon="mdi:camera-plus" />
            <span>上传封面</span>
          </div>
        </div>
        <el-input v-model="editName" placeholder="歌单名称" maxlength="50" />
        <el-input v-model="editDescription" type="textarea" :rows="3" placeholder="简介" maxlength="200" />
        <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
      </div>
      <template #footer>
        <el-button @click="editDialog = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="save">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute } from 'vue-router'
import { usePlaylistStore } from '../stores/playlistStore'
import MusicList from '../components/MusicList.vue'
import * as fileAPI from '../api/file'
import { ElMessage } from 'element-plus'

const route = useRoute()
const playlistStore = usePlaylistStore()
const playlist = ref(null)

// 封面：优先自定义封面，否则用最近添加的一首的封面
const coverUrl = computed(() => {
  if (playlist.value?.coverUrl) return playlist.value.coverUrl
  const songs = playlist.value?.songs || []
  return songs.length ? songs[songs.length - 1].coverUrl : ''
})

const editDialog = ref(false)
const editName = ref('')
const editDescription = ref('')
const coverPreview = ref('')
const coverFile = ref(null)
const fileInput = ref(null)
const saving = ref(false)

async function load() {
  playlist.value = await playlistStore.getDetail(route.params.id)
}

// 切换歌单时只是 :id 变化，组件被复用不会重新 onMounted，
// 必须 watch 路由参数重新加载，否则从 A 切到 B 时仍显示 A 的内容
watch(() => route.params.id, load, { immediate: true })

function openEdit() {
  editName.value = playlist.value.name
  editDescription.value = playlist.value.description || ''
  coverPreview.value = playlist.value.coverUrl || ''
  coverFile.value = null
  editDialog.value = true
}

function pickCover() {
  fileInput.value?.click()
}

function onFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  coverFile.value = file
  coverPreview.value = URL.createObjectURL(file)
}

async function save() {
  if (!editName.value.trim()) {
    ElMessage.warning('歌单名称不能为空')
    return
  }
  saving.value = true
  try {
    // 先传封面（若有），拿到 MinIO 地址后再更新歌单
    let coverUrl = playlist.value.coverUrl
    if (coverFile.value) {
      const formData = new FormData()
      formData.append('file', coverFile.value)
      const data = await fileAPI.uploadImage(formData)
      coverUrl = data.url
    }
    await playlistStore.update(playlist.value.id, { name: editName.value.trim(), coverUrl, description: editDescription.value.trim() })
    await load()
    editDialog.value = false
    ElMessage.success('已保存')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.pl-header {
  display: flex; align-items: flex-end; gap: 24px;
  margin-bottom: 24px;
}
.pl-cover {
  position: relative; width: 180px; height: 180px; border-radius: 12px;
  overflow: hidden; cursor: pointer; flex-shrink: 0;
  background: var(--bg-secondary);
}
.pl-cover img { width: 100%; height: 100%; object-fit: cover; }
.pl-cover-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  font-size: 64px; color: var(--text-muted);
}
.pl-cover-mask {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  background: rgba(0, 0, 0, 0.45); color: #fff; font-size: 14px;
  opacity: 0; transition: opacity 0.2s;
}
.pl-cover:hover .pl-cover-mask { opacity: 1; }
.pl-info h2 { margin: 0 0 8px; font-size: 28px; }
.pl-meta { margin: 0 0 16px; color: var(--text-muted); font-size: 14px; }
.pl-desc { margin: 0 0 16px; color: var(--text-secondary); font-size: 14px; }

.edit-body { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.edit-cover {
  width: 120px; height: 120px; border-radius: 8px; overflow: hidden;
  cursor: pointer; background: var(--bg-secondary);
  display: flex; align-items: center; justify-content: center;
}
.edit-cover img { width: 100%; height: 100%; object-fit: cover; }
.edit-cover-empty {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  color: var(--text-muted); font-size: 12px;
}
.edit-cover-empty .iconify { font-size: 28px; }
</style>
