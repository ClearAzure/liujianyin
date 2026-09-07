<template>
  <aside class="sidebar">
    <nav class="nav-section">
      <router-link to="/" class="nav-item" exact-active-class="active">
        <Icon icon="mdi:home" /> 推荐
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
        @click="$router.push(`/playlist/${pl.id}`)">
        <!-- $router.push路由跳转 -->
        <img v-if="pl.coverUrl" :src="pl.coverUrl" class="pl-thumb" alt="" />
        <Icon v-else icon="mdi:playlist-music" />
        <span class="pl-name">{{ pl.name }}</span>
      </div>

      <div class="nav-item create-btn" @click="showCreate = true">
        <Icon icon="mdi:plus" /> 创建新歌单
      </div>

    </div>

    <div class="nav-section">
      <div class="section-title">管理</div>
      <router-link to="/upload" class="nav-item" active-class="active">
        <Icon icon="mdi:upload" /> 上传歌曲
      </router-link>
    </div>

    <!-- 创建歌单弹窗 -->
    <el-dialog v-model="showCreate" title="创建歌单" width="320px" align-center>
      <el-input v-model="newName" placeholder="歌单名称" @keyup.enter="doCreate" />
      <template #footer>
        <el-button @click="showCreate = false">取消</el-button>
        <el-button type="danger" @click="doCreate">确认创建</el-button>
      </template>
    </el-dialog>

  </aside>
</template>

<script setup>
import { ref } from 'vue'
import { usePlaylistStore } from '../stores/playlistStore'
import { onMounted } from 'vue'

// 获取歌单列表
const playlistStore = usePlaylistStore()
// 控制创建歌单弹窗显示
const showCreate = ref(false)
// 歌单名称
const newName = ref('')

onMounted(() => {
  playlistStore.fetchMyPlaylists()
})

// 创建歌单
async function doCreate() {
  if (newName.value.trim()) {
    await playlistStore.create(newName.value.trim())
    
    newName.value = ''
    showCreate.value = false
  }
}
</script>

<style scoped>
.pl-thumb {
  width: 20px; height: 20px; border-radius: 4px; object-fit: cover;
  vertical-align: middle; margin-right: 6px;
}
.pl-name { vertical-align: middle; }
</style>
