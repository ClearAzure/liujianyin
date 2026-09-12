<template>
  <div class="page-profile">
    <button class="role-toggle-btn" @click="toggleRole">
      <Icon icon="mdi:swap-horizontal" /> {{ userStore.isAdmin ? '切换用户' : '切换管理员' }}
    </button>
    <!-- 头部：大头像 + 姓名 -->
    <div class="profile-header">
      <div class="profile-avatar" @click="pickAvatar" :title="avatarUploading ? '上传中…' : '点击更换头像'">
        <img v-if="userStore.userInfo?.avatarUrl" :src="userStore.userInfo.avatarUrl" alt="头像" />
        <div v-else class="profile-avatar-placeholder">{{ initial }}</div>
        <div class="profile-avatar-mask">
          <Icon v-if="avatarUploading" icon="mdi:loading" class="spin" />
          <template v-else><Icon icon="mdi:camera-plus" /> 更换头像</template>
        </div>
      </div>
      <!-- 隐藏起来的文件选择器 , 作为头像上传的触发器 -->
      <input ref="avatarInput" type="file" accept="image/*" hidden @change="onAvatarChange" />

      <div class="profile-info">
        <div class="profile-name-row">
          <h1>{{ userStore.userInfo?.nickname || userStore.userInfo?.username }}</h1>
          <button class="edit-name-btn" @click="editName" title="修改姓名">
            <Icon icon="mdi:pencil" />
          </button>
        </div>
        <p class="profile-username">@{{ userStore.userInfo?.username }}</p>
        <div class="profile-signature-row">
          <p class="profile-signature">{{ userStore.userInfo?.signature || '这个人很懒，还没有签名~' }}</p>
          <button class="edit-name-btn" @click="editSignature" title="修改签名">
            <Icon icon="mdi:pencil" />
          </button>
        </div>
      </div>
    </div>

    <!-- 歌单卡片：我喜欢的音乐固定第一个，后面跟自定义歌单 -->
    <div class="section">
      <h2>我的歌单</h2>
      <div class="card-grid">
        <PlaylistCard
          name="我喜欢的音乐"
          :cover-url="favoriteCover"
          :count="favoriteStore.favoriteSongs.length"
          icon="mdi:heart"
          @click="$router.push('/favorite')"
        />
        <PlaylistCard
          v-for="pl in playlistStore.myPlaylists"
          :key="pl.id"
          :name="pl.name"
          :cover-url="pl.coverUrl || lastSongCover(pl)"
          :count="pl.songs?.length || 0"
          icon="mdi:playlist-music"
          @click="$router.push(`/playlist/${pl.id}`)"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '../stores/userStore'
import { usePlaylistStore } from '../stores/playlistStore'
import { useFavoriteStore } from '../stores/favoriteStore'
import PlaylistCard from '../components/PlaylistCard.vue'
import * as fileAPI from '../api/file'
import { ElMessage, ElMessageBox } from 'element-plus'

const userStore = useUserStore()
const playlistStore = usePlaylistStore()
const favoriteStore = useFavoriteStore()

const avatarInput = ref(null)
const avatarUploading = ref(false)

const initial = computed(() => (userStore.userInfo?.nickname || userStore.userInfo?.username || '?').charAt(0))
// 我喜欢的音乐没有自定义封面，用“最近收藏的一首”的封面（收藏列表是倒序，第一条即最近）
const favoriteCover = computed(() => favoriteStore.favoriteSongs[0]?.coverUrl || '')

onMounted(() => {
  playlistStore.fetchMyPlaylists()
  favoriteStore.fetchFavorites()
})

// 自定义歌单无封面时，用“最近添加的一首”的封面（后端按添加顺序返回，最后一条即最近）
function lastSongCover(pl) {
  const songs = pl.songs || []
  return songs.length ? songs[songs.length - 1].coverUrl : ''
}

// ---- 更换头像 ----
function pickAvatar() {
  //模拟点击这个 <input type="file">,通过 ref 操作真实 DOM
  avatarInput.value?.click()
}

async function onAvatarChange(e) {
  const file = e.target.files[0]
  if (!file) return
  avatarUploading.value = true
  try {
    const formData = new FormData()
    formData.append('file', file)
    const data = await fileAPI.uploadImage(formData)
    await userStore.update({ avatarUrl: data.url })
    ElMessage.success('头像已更新')
  } catch (err) {
    ElMessage.error(err.message || '头像更新失败')
  } finally {
    avatarUploading.value = false
    e.target.value = ''
  }
}

// ---- 修改姓名 ----
async function editName() {
  const current = userStore.userInfo?.nickname || ''
  try {
    const { value } = await ElMessageBox.prompt('请输入新的姓名', '修改姓名', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputValue: current,
      inputValidator: (v) => (v && v.trim() ? true : '姓名不能为空')
    })
    await userStore.update({ nickname: value.trim() })
    ElMessage.success('姓名已更新')
  } catch { /* 取消 */ }
}

// ---- 修改签名 ----
async function editSignature() {
  const current = userStore.userInfo?.signature || ''
  try {
    const { value } = await ElMessageBox.prompt('请输入个性签名', '修改签名', {
      confirmButtonText: '保存',
      cancelButtonText: '取消',
      inputValue: current,
      inputType: 'textarea',
      inputValidator: (v) => (v == null || v.trim().length <= 200 ? true : '签名不能超过 200 字')
    })
    await userStore.update({ signature: value.trim() })
    ElMessage.success('签名已更新')
  } catch { /* 取消 */ }
}

// ---- 切换角色（演示用） ----
async function toggleRole() {
  try {
    const target = userStore.isAdmin ? '普通用户' : '管理员'
    await userStore.toggleRole()
    ElMessage.success(`已切换为${target}`)
  } catch (err) {
    ElMessage.error(err.message || '切换失败')
  }
}
</script>

<style scoped>
.page-profile { position: relative; }
.role-toggle-btn {
  position: absolute; top: 0; right: 0;
  display: inline-flex; align-items: center; gap: 6px;
  background: var(--bg-card); color: var(--text-primary);
  border: 1px solid var(--border-color); border-radius: 6px;
  padding: 8px 16px; cursor: pointer; font-size: 14px;
  transition: background 0.2s, border-color 0.2s;
}
.role-toggle-btn:hover { background: var(--bg-hover); border-color: var(--text-muted); }

.profile-header {
  display: flex; align-items: center; gap: 28px;
  margin-bottom: 32px;
}
.profile-avatar {
  position: relative; width: 140px; height: 140px; border-radius: 50%;
  overflow: hidden; cursor: pointer; flex-shrink: 0;
  background: var(--bg-secondary);
}
.profile-avatar img { width: 100%; height: 100%; object-fit: cover; }
.profile-avatar-placeholder {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  font-size: 56px; font-weight: bold; color: #fff; background: var(--accent);
}
.profile-avatar-mask {
  position: absolute; inset: 0; border-radius: 50%;
  display: flex; align-items: center; justify-content: center; gap: 6px;
  background: rgba(0, 0, 0, 0.45); color: #fff; font-size: 14px;
  opacity: 0; transition: opacity 0.2s;
}
.profile-avatar:hover .profile-avatar-mask { opacity: 1; }
.spin { animation: spin 1s linear infinite; font-size: 22px; }
@keyframes spin { to { transform: rotate(360deg); } }

.profile-info { display: flex; flex-direction: column; gap: 8px; }
.profile-name-row { display: flex; align-items: center; gap: 10px; }
.profile-name-row h1 { margin: 0; font-size: 30px; }
.edit-name-btn {
  border: none; background: transparent; cursor: pointer;
  color: var(--text-secondary); width: 32px; height: 32px;
  display: inline-flex; align-items: center; justify-content: center;
  border-radius: 50%; font-size: 20px; padding: 0;
}
.edit-name-btn:hover { color: var(--text-primary); background: rgba(255, 255, 255, 0.1); }
.profile-username { margin: 0; color: var(--text-muted); font-size: 14px; }
.profile-signature-row { display: flex; align-items: center; gap: 10px; }
.profile-signature { margin: 0; color: var(--text-secondary); font-size: 14px; }
</style>
