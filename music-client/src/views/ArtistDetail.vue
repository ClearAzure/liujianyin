<template>
  <div class="page-detail" v-if="artist">
    <div class="artist-header">
      <div class="artist-avatar" @click="openEdit" :title="userStore.isAdmin ? '编辑歌手' : ''">
        <img v-if="artist.avatarUrl" :src="artist.avatarUrl" alt="avatar" />
        <Icon v-else icon="mdi:account-music" />
        <div v-if="userStore.isAdmin" class="avatar-mask">
          <Icon icon="mdi:pencil" /> 编辑
        </div>
      </div>
      <div class="artist-info">
        <h2>{{ artist.name }}</h2>
        <p v-if="artist.description" class="desc">{{ artist.description }}</p>
        <p class="meta">{{ artist.musicCount || 0 }} 首歌曲</p>
        <el-button v-if="userStore.isAdmin" type="danger" round @click="openEdit">
          <Icon icon="mdi:pencil" /> 编辑歌手
        </el-button>
      </div>
    </div>

    <MusicList :songs="artist.songs || []" @deleted="load" />

    <!-- 编辑歌手弹窗：改名/简介/头像 + 删除 -->
    <el-dialog v-model="editDialog" title="编辑歌手" width="400px" align-center>
      <div class="edit-body">
        <div class="edit-avatar" @click="pickAvatar">
          <img v-if="avatarPreview" :src="avatarPreview" alt="avatar" />
          <div v-else class="edit-avatar-empty">
            <Icon icon="mdi:camera-plus" />
            <span>上传头像</span>
          </div>
        </div>
        <el-input v-model="editName" placeholder="歌手名称" maxlength="50" />
        <el-input v-model="editDescription" type="textarea" :rows="3" placeholder="简介" maxlength="200" />
        <input ref="fileInput" type="file" accept="image/*" hidden @change="onFileChange" />
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="danger" plain @click="confirmDelete">删除歌手</el-button>
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
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '../stores/userStore'
import MusicList from '../components/MusicList.vue'
import * as artistAPI from '../api/artist'
import * as fileAPI from '../api/file'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const artist = ref(null)

const editDialog = ref(false)
const editName = ref('')
const editDescription = ref('')
const avatarPreview = ref('')
const avatarFile = ref(null)
const fileInput = ref(null)
const saving = ref(false)

async function load() {
  try {
    artist.value = await artistAPI.getDetail(route.params.id)
  } catch {
    artist.value = null
  }
}
watch(() => route.params.id, load, { immediate: true })

function openEdit() {
  if (!userStore.isLogin) { userStore.openLogin(); return }
  if (!userStore.isAdmin) return
  editName.value = artist.value.name
  editDescription.value = artist.value.description || ''
  avatarPreview.value = artist.value.avatarUrl || ''
  avatarFile.value = null
  editDialog.value = true
}

function pickAvatar() {
  fileInput.value?.click()
}

function onFileChange(e) {
  const file = e.target.files[0]
  if (!file) return
  avatarFile.value = file
  avatarPreview.value = URL.createObjectURL(file)
}

async function save() {
  if (!editName.value.trim()) {
    ElMessage.warning('歌手名称不能为空')
    return
  }
  saving.value = true
  try {
    let avatarUrl = artist.value.avatarUrl
    if (avatarFile.value) {
      const fd = new FormData()
      fd.append('file', avatarFile.value)
      const data = await fileAPI.uploadImage(fd)
      avatarUrl = data.url
    }
    await artistAPI.update(artist.value.id, { name: editName.value.trim(), avatarUrl, description: editDescription.value.trim() })
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
    await ElMessageBox.confirm('确定删除该歌手吗？TA 名下的所有歌曲和专辑也会一并删除。', '删除歌手', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      confirmButtonClass: 'el-button--danger'
    })
  } catch {
    return
  }
  try {
    await artistAPI.remove(artist.value.id)
    ElMessage.success('歌手已删除')
    router.push('/artists')
  } catch (err) {
    ElMessage.error(err.message || '删除失败')
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

.artist-info .el-button {
  margin-top: 10px;
}

.edit-body { display: flex; flex-direction: column; align-items: center; gap: 16px; }
.edit-avatar {
  width: 120px; height: 120px; border-radius: 50%; overflow: hidden;
  cursor: pointer; background: var(--bg-secondary);
  display: flex; align-items: center; justify-content: center;
}
.edit-avatar img { width: 100%; height: 100%; object-fit: cover; }
.edit-avatar-empty {
  display: flex; flex-direction: column; align-items: center; gap: 4px;
  color: var(--text-muted); font-size: 12px;
}
.edit-avatar-empty .iconify { font-size: 28px; }

.dialog-footer { display: flex; justify-content: space-between; align-items: center; }
.dialog-footer > div { display: flex; gap: 8px; }
</style>
