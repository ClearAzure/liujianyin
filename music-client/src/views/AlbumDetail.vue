<template>
  <div class="page-detail" v-if="album">
    <div class="album-header">
      <div class="album-cover" @click="openEdit" :title="userStore.isAdmin ? '编辑专辑' : ''">
        <img v-if="coverUrl" :src="coverUrl" alt="cover" />
        <Icon v-else icon="mdi:album" />
        <div v-if="userStore.isAdmin" class="cover-mask">
          <Icon icon="mdi:pencil" /> 编辑
        </div>
      </div>
      <div class="album-info">
        <h2>{{ album.name }}</h2>
        <p class="meta">
          歌手：
          <router-link v-if="album.artistId" :to="`/artist/${album.artistId}`" class="link">{{ album.artistName
            }}</router-link>
          <template v-else>{{ album.artistName }}</template>
        </p>
        <p v-if="album.description" class="meta">简介：{{ album.description }}</p>
        <p v-if="album.publishTime" class="meta">发布时间：{{ album.publishTime }}</p>
        <p class="meta">{{ album.musicCount || 0 }} 首歌曲</p>
        <el-button v-if="userStore.isAdmin" type="danger" round @click="openEdit">
          <Icon icon="mdi:pencil" /> 编辑专辑
        </el-button>
      </div>
    </div>

    <MusicList :songs="album.songs || []" @deleted="load" />

    <!-- 编辑专辑弹窗：改名/简介/封面 + 删除 -->
    <el-dialog v-model="editDialog" title="编辑专辑" width="400px" align-center>
      <div class="edit-body">
        <div class="edit-cover" @click="pickCover">
          <img v-if="coverPreview" :src="coverPreview" alt="cover" />
          <div v-else class="edit-cover-empty">
            <Icon icon="mdi:camera-plus" />
            <span>上传封面</span>
          </div>
        </div>
        <el-input v-model="editName" placeholder="专辑名称" maxlength="50" />
        <el-input v-model="editDescription" type="textarea" :rows="3" placeholder="简介" maxlength="200" />
        <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" plain @click="confirmDelete">删除专辑</el-button>
          <div>
            <el-button @click="editDialog = false">取消</el-button>
            <el-button type="danger" :loading="saving" @click="save">保存</el-button>
          </div>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import MusicList from '../components/MusicList.vue'
import * as albumAPI from '../api/album'
import * as fileAPI from '../api/file'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const album = ref(null)

// 封面：优先专辑自定义封面，否则用第一首歌的封面
const coverUrl = computed(() => {
  if (album.value?.coverUrl) return album.value.coverUrl
  const songs = album.value?.songs || []
  return songs.length ? songs[0].coverUrl : ''
})

const editDialog = ref(false)
const editName = ref('')
const editDescription = ref('')
const coverPreview = ref('')
const coverFile = ref(null)
const fileInput = ref(null)
const saving = ref(false)

async function load() {
  try {
    album.value = await albumAPI.getDetail(route.params.id)
  } catch {
    album.value = null
  }
}
watch(() => route.params.id, load, { immediate: true })

function openEdit() {
  if (!userStore.isLogin) { userStore.openLogin(); return }
  if (!userStore.isAdmin) return
  editName.value = album.value.name
  editDescription.value = album.value.description || ''
  coverPreview.value = album.value.coverUrl || ''
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
    ElMessage.warning('专辑名称不能为空')
    return
  }
  saving.value = true
  try {
    let coverUrl = album.value.coverUrl
    if (coverFile.value) {
      const fd = new FormData()
      fd.append('file', coverFile.value)
      const data = await fileAPI.uploadImage(fd)
      coverUrl = data.url
    }
    await albumAPI.update(album.value.id, { name: editName.value.trim(), coverUrl, description: editDescription.value.trim() })
    await load()
    editDialog.value = false
    ElMessage.success('已保存')
  } catch (err) {
    ElMessage.error(err.message || '保存失败')
  } finally {
    saving.value = false
  }
}

async function confirmDelete() {
  try {
    await ElMessageBox.confirm('确定删除该专辑吗？专辑里的歌曲会保留（解除专辑归属）。', '删除专辑', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    })
  } catch {
    return
  }
  try {
    await albumAPI.remove(album.value.id)
    ElMessage.success('专辑已删除')
    router.push('/albums')
  } catch (err) {
    ElMessage.error(err.message || '删除失败')
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

.album-info .el-button {
  margin-top: 10px;
}

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

.dialog-footer { display: flex; justify-content: space-between; align-items: center; }
.dialog-footer > div { display: flex; gap: 8px; }
</style>
