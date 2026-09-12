<template>
  <aside class="sidebar">
    <nav class="nav-section">
      <router-link to="/" class="nav-item" exact-active-class="active">
        <Icon icon="mdi:home" /> 推荐
      </router-link>
      <router-link to="/artists" class="nav-item" active-class="active">
        <Icon icon="mdi:account-music" /> 歌手
      </router-link>
      <router-link to="/albums" class="nav-item" active-class="active">
        <Icon icon="mdi:album" /> 专辑
      </router-link>
      <router-link to="/favorite" class="nav-item" active-class="active">
        <Icon icon="mdi:heart" /> 我喜欢的音乐
      </router-link>
      <router-link to="/history" class="nav-item" active-class="active">
        <Icon icon="mdi:history" /> 最近播放
      </router-link>
    </nav>

    <div class="nav-section">
      <div class="section-title">我的歌单</div>
      <div v-for="pl in playlistStore.myPlaylists" :key="pl.id" class="nav-item"
        @click="$router.push(`/playlist/${pl.id}`)"
        @contextmenu.prevent="openMenu($event, pl)">
        <img v-if="coverOf(pl)" :src="coverOf(pl)" class="pl-thumb" alt="" />
        <Icon v-else icon="mdi:playlist-music" />
        <span class="pl-name">{{ pl.name }}</span>
      </div>

      <div class="nav-item create-btn" @click="showCreate = true">
        <Icon icon="mdi:plus" /> 创建新歌单
      </div>
    </div>

    <div class="nav-section" v-if="userStore.isAdmin">
      <div class="section-title">管理</div>
      <router-link to="/upload" class="nav-item" active-class="active">
        <Icon icon="mdi:upload" /> 上传歌曲
      </router-link>
    </div>

    <!-- 创建歌单弹窗 -->
    <el-dialog v-model="showCreate" title="创建歌单" width="360px" align-center>
      <div class="edit-body">
        <div class="edit-cover" @click="pickCreateCover">
          <img v-if="newCoverPreview" :src="newCoverPreview" alt="cover" />
          <div v-else class="edit-cover-empty">
            <Icon icon="mdi:camera-plus" />
            <span>上传封面（可选）</span>
          </div>
        </div>
        <input ref="createFileInput" type="file" accept="image/*" hidden @change="onCreateCoverChange" />
        <el-input v-model="newName" placeholder="歌单名称" maxlength="50" @keyup.enter="doCreate" />
        <el-input v-model="newDescription" type="textarea" :rows="3" placeholder="简介（可选）" maxlength="200" />
      </div>
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="danger" :loading="creating" @click="doCreate">确认创建</el-button>
      </template>
    </el-dialog>

    <!-- 编辑歌单弹窗 -->
    <el-dialog v-model="showEdit" title="编辑歌单" width="360px" align-center>
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
        <input ref="fileInput" type="file" accept="image/*" hidden @change="onCoverChange" />
      </div>
      <template #footer>
        <el-button @click="showEdit = false">取消</el-button>
        <el-button type="danger" :loading="saving" @click="saveEdit">保存</el-button>
      </template>
    </el-dialog>

    <!-- 右键菜单 -->
    <div v-if="menu.visible" class="context-menu" :style="menuStyle">
      <div class="menu-item" @click="editPlaylist">
        <Icon icon="mdi:pencil" /> 编辑歌单
      </div>
      <div class="menu-item danger" @click="confirmDelete">
        <Icon icon="mdi:delete" /> 删除歌单
      </div>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { usePlaylistStore } from '../stores/playlistStore'
import { useUserStore } from '../stores/userStore'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as fileAPI from '../api/file'

const route = useRoute()
const router = useRouter()

// 获取歌单列表
const playlistStore = usePlaylistStore()
const userStore = useUserStore()
// 控制创建歌单弹窗显示
const showCreate = ref(false)
// 歌单名称
const newName = ref('')
// 创建歌单：封面 / 简介
const newCoverPreview = ref('')
const newCoverFile = ref(null)
const newDescription = ref('')
const createFileInput = ref(null)
const creating = ref(false)

// 右键菜单状态
const menu = ref({ visible: false, x: 0, y: 0 })
// 右键选中的歌单
const activePlaylist = ref(null)

// 编辑弹窗状态
const showEdit = ref(false)
const editId = ref(null)
const editName = ref('')
const editDescription = ref('')
const editCover = ref('')
const coverPreview = ref('')
const coverFile = ref(null)
const fileInput = ref(null)
const saving = ref(false)

// 菜单定位（并限制在视口内）
const menuStyle = computed(() => {
  const x = Math.min(menu.value.x, window.innerWidth - 160)
  const y = Math.min(menu.value.y, window.innerHeight - 100)
  return { left: x + 'px', top: y + 'px' }
})

onMounted(() => {
  playlistStore.fetchMyPlaylists()
  document.addEventListener('click', closeMenu)
})

onUnmounted(() => {
  document.removeEventListener('click', closeMenu)
})

// 歌单封面：优先自定义封面，否则用最近添加的一首的封面
function coverOf(pl) {
  if (pl.coverUrl) return pl.coverUrl
  const songs = pl.songs || []
  return songs.length ? songs[songs.length - 1].coverUrl : ''
}

// 创建歌单（支持封面 + 简介）
async function doCreate() {
  if (!newName.value.trim()) {
    ElMessage.warning('歌单名称不能为空')
    return
  }
  creating.value = true
  try {
    // 若选了封面，先上传拿到 URL，再带着一起创建
    let coverUrl = ''
    if (newCoverFile.value) {
      const fd = new FormData()
      fd.append('file', newCoverFile.value)
      const data = await fileAPI.uploadImage(fd)
      coverUrl = data.url
    }
    await playlistStore.create({
      name: newName.value.trim(),
      coverUrl,
      description: newDescription.value.trim()
    })
    // 重置弹窗
    newName.value = ''
    newDescription.value = ''
    newCoverFile.value = null
    newCoverPreview.value = ''
    showCreate.value = false
    ElMessage.success('歌单已创建')
  } catch (e) {
    ElMessage.error(e.message || '创建失败')
  } finally {
    creating.value = false
  }
}

// 打开右键菜单
function openMenu(e, pl) {
  activePlaylist.value = pl
  menu.value = { visible: true, x: e.clientX, y: e.clientY }
}

function closeMenu() {
  menu.value.visible = false
}

// 编辑歌单
function editPlaylist() {
  const pl = activePlaylist.value
  if (!pl) return
  editId.value = pl.id
  editName.value = pl.name
  editDescription.value = pl.description || ''
  editCover.value = pl.coverUrl || ''
  coverPreview.value = pl.coverUrl || ''
  coverFile.value = null
  showEdit.value = true
  closeMenu()
}

function pickCover() {
  fileInput.value?.click()
}

function onCoverChange(e) {
  const file = e.target.files[0]
  if (!file) return
  coverFile.value = file
  coverPreview.value = URL.createObjectURL(file)
}

// 创建弹窗选封面
function pickCreateCover() {
  createFileInput.value?.click()
}
function onCreateCoverChange(e) {
  const file = e.target.files[0]
  if (!file) return
  newCoverFile.value = file
  newCoverPreview.value = URL.createObjectURL(file)
}

async function saveEdit() {
  if (!editName.value.trim()) {
    ElMessage.warning('歌单名称不能为空')
    return
  }
  saving.value = true
  try {
    // 先传封面（若有），拿到 MinIO 地址后再更新歌单
    let coverUrl = editCover.value
    if (coverFile.value) {
      const fd = new FormData()
      fd.append('file', coverFile.value)
      const data = await fileAPI.uploadImage(fd)
      coverUrl = data.url
    }
    await playlistStore.update(editId.value, { name: editName.value.trim(), coverUrl, description: editDescription.value.trim() })
    showEdit.value = false
    ElMessage.success('已保存')
  } catch (e) {
    ElMessage.error(e.message || '保存失败')
  } finally {
    saving.value = false
  }
}

// 删除歌单（二次确认）
async function confirmDelete() {
  const pl = activePlaylist.value
  if (!pl) return
  closeMenu()
  try {
    await ElMessageBox.confirm(`确定要删除歌单「${pl.name}」吗？此操作不可恢复。`, '删除歌单', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return // 用户取消
  }
  await playlistStore.remove(pl.id)
  ElMessage.success('歌单已删除')
  // 若当前正在该歌单详情页，跳回首页
  if (route.path === `/playlist/${pl.id}`) {
    router.replace('/')
  }
}
</script>

<style scoped>
.pl-thumb {
  width: 20px; height: 20px; border-radius: 4px; object-fit: cover;
  vertical-align: middle; margin-right: 6px;
}
.pl-name { vertical-align: middle; }

.context-menu {
  position: fixed;
  z-index: 3000;
  min-width: 140px;
  background: var(--bg-card);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4);
  padding: 4px;
}
.menu-item {
  display: flex; align-items: center; gap: 8px;
  padding: 8px 12px; border-radius: 6px; cursor: pointer;
  font-size: 14px; color: var(--text-primary);
}
.menu-item:hover { background: var(--bg-hover); }
.menu-item.danger { color: var(--el-color-danger); }

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
