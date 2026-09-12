<template>
  <!-- 通用“…”更多操作浮窗：目前只有“添加到歌单”，后续可继续往里加功能 -->
  <el-popover ref="popRef" trigger="click" placement="top-end" :width="230" @show="onShow">
    <template #reference>
      <button class="song-menu-trigger" @click.stop title="更多操作">
        <Icon icon="mdi:dots-horizontal" />
      </button>
    </template>

    <div class="menu-body">
      <template v-if="playlistId">
        <div class="menu-item menu-danger" @click="removeFromPlaylist">
          <Icon icon="mdi:playlist-remove" />
          <span>移除当前歌单</span>
        </div>
      </template>
      <template v-if="userStore.isAdmin">
        <div class="menu-item menu-danger" @click="deleteMusic">
          <Icon icon="mdi:delete" />
          <span>删除歌曲</span>
        </div>
      </template>
      <div v-if="playlistId || userStore.isAdmin" class="menu-divider"></div>

      <div class="menu-title">添加到歌单</div>
      <div v-if="!playlistStore.myPlaylists.length" class="menu-empty">还没有歌单，先创建一个吧</div>
      <ul v-else class="menu-list">
        <li v-for="pl in playlistStore.myPlaylists" :key="pl.id" class="menu-item" @click="addTo(pl)">
          <Icon icon="mdi:playlist-music" />
          <span class="menu-item-name">{{ pl.name }}</span>
        </li>
      </ul>
      <div class="menu-divider"></div>
      <div class="menu-item menu-create" @click="createNew">
        <Icon icon="mdi:plus" />
        <span>新建歌单</span>
      </div>
    </div>
  </el-popover>
</template>

<script setup>
import { ref } from 'vue'
import { usePlaylistStore } from '../stores/playlistStore'
import { useUserStore } from '../stores/userStore'
import { ElMessage, ElMessageBox } from 'element-plus'
import * as musicAPI from '../api/music'

const props = defineProps({
  music: { type: Object, required: true },
  // 传入当前歌单 id 时，菜单额外显示“移除当前歌单”
  playlistId: { type: [Number, String], default: null }
})

const emit = defineEmits(['removed', 'deleted'])

const playlistStore = usePlaylistStore()
const userStore = useUserStore()
const popRef = ref(null)

// 打开浮窗时：未登录直接弹登录框；已登录则刷新我的歌单
function onShow() {
  if (!userStore.isLogin) {
    userStore.openLogin()
    popRef.value?.hide()
    return
  }
  playlistStore.fetchMyPlaylists()
}

async function addTo(pl) {
  if (!userStore.isLogin) return
  try {
    await playlistStore.addMusic(pl.id, props.music.id)
    ElMessage.success(`已添加到「${pl.name}」`)
    popRef.value?.hide()
  } catch (e) {
    ElMessage.error(e.message || '添加失败')
  }
}

async function createNew() {
  if (!userStore.isLogin) return
  try {
    const { value } = await ElMessageBox.prompt('请输入新歌单名称', '新建歌单', {
      confirmButtonText: '创建',
      cancelButtonText: '取消',
      inputValidator: (v) => (v && v.trim() ? true : '名称不能为空')
    })
    const pl = await playlistStore.create({ name: value.trim() })
    await addTo(pl)
  } catch { /* 取消创建 */ }
}

async function removeFromPlaylist() {
  try {
    await playlistStore.removeMusic(props.playlistId, props.music.id)
    ElMessage.success('已从歌单移除')
    popRef.value?.hide()
    emit('removed')
  } catch (e) {
    ElMessage.error(e.message || '移除失败')
  }
}

async function deleteMusic() {
  try {
    await ElMessageBox.confirm(`确定删除歌曲「${props.music.name}」吗？此操作不可恢复。`, '删除歌曲', {
      type: 'warning',
      confirmButtonText: '删除',
      cancelButtonText: '取消',
    })
  } catch {
    return // 用户取消
  }
  try {
    await musicAPI.remove(props.music.id)
    ElMessage.success('歌曲已删除')
    popRef.value?.hide()
    emit('deleted')
  } catch (e) {
    ElMessage.error(e.message || '删除失败')
  }
}
</script>

<style scoped>
.song-menu-trigger {
  border: none;
  background: transparent;
  color: var(--text-secondary);
  cursor: pointer;
  width: 28px;
  height: 28px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  font-size: 20px;
  padding: 0;
}

.song-menu-trigger:hover {
  color: var(--text-primary);
  background: rgba(255, 255, 255, 0.1);
}

.menu-body {
  padding: 4px 0;
}

.menu-title {
  font-size: 12px;
  color: var(--text-muted);
  padding: 4px 10px 8px;
}

.menu-list {
  list-style: none;
  margin: 0;
  padding: 0;
  max-height: 220px;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 10px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  color: var(--text-primary);
}

.menu-item:hover {
  background: var(--bg-hover);
}

.menu-item :deep(svg) {
  flex-shrink: 0;
}

.menu-item-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.menu-divider {
  height: 1px;
  background: var(--border-color);
  margin: 6px 0;
}

.menu-create {
  color: var(--accent);
}

.menu-empty {
  padding: 12px 10px;
  color: var(--text-muted);
  font-size: 13px;
}

.menu-danger {
  color: #f56c6c;
}
</style>
