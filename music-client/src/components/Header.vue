<template>
  <header class="app-header">
    <div class="header-left">

      <div class="window-controls">
        <span class="logo">
          <Icon icon="mdi:music" /> 琉涧音
        </span>
      </div>

      <div class="nav-btns">
        <button class="nav-btn" @click="$router.back()" title="返回">&lt;</button>
        <button class="nav-btn" @click="$router.forward()" title="前进">&gt;</button>
      </div>

      <!-- 搜索框 -->
      <div class="search-box">
        <el-input v-model="keyword" placeholder="搜索音乐、歌手..." size="small" :prefix-icon="Search" @keyup.enter="doSearch"
          clearable />
      </div>
    </div>

    <!-- 右侧用户信息和窗口控制按钮 -->
    <div class="header-right">

<!-- 用户已登录(根据token是否为空)，显示用户信息和退出按钮 -->
 
      <template v-if="userStore.isLogin">
        <div class="user-info" @click="$router.push('/profile')" title="个人主页">
          <img v-if="userStore.userInfo?.avatarUrl" :src="userStore.userInfo.avatarUrl" class="user-avatar-img" alt="头像" />
          <span v-else class="user-avatar">{{ userStore.userInfo?.nickname?.charAt(0) || '?' }}</span>
          <span class="user-name">{{ userStore.userInfo?.nickname }}</span>
        </div>
        <el-button text size="small" @click="userStore.logout()">退出</el-button>
      </template>

      <template v-else>
        <el-button text size="small" @click="userStore.openLogin()">登录</el-button>
      </template>

      <div class="window-btns">
        <button class="win-btn" @click="minimize" title="最小化">
          <Icon icon="mdi:minus" />
        </button>
        <button class="win-btn" @click="maximize" title="最大化">
          <Icon icon="mdi:checkbox-blank-outline" />
        </button>
        <button class="win-btn win-close" @click="closeWin" title="关闭">
          <Icon icon="mdi:close" />
        </button>
      </div>
    </div>

    <!-- 登录/注册弹窗 -->
    <el-dialog v-model="userStore.showLogin" :title="isRegisterMode ? '注册' : '登录'" width="360px" align-center
      :close-on-click-modal="false">
      <el-form @submit.prevent="handleLogin" label-position="top">

        <el-form-item>
          <el-input v-model="loginForm.username" placeholder="用户名" size="large" />
        </el-form-item>

        <el-form-item v-if="isRegisterMode">
          <el-input v-model="loginForm.email" placeholder="邮箱" type="email" size="large" />
        </el-form-item>

        <el-form-item>
          <el-input v-model="loginForm.password" placeholder="密码" type="password" size="large" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="danger" size="large" style="width:100%" @click="handleLogin" :loading="loginLoading">
            {{ isRegisterMode ? '注册' : '登录' }}
          </el-button>
        </el-form-item>
        <el-button text size="small" style="width:100%" @click="isRegisterMode = !isRegisterMode">
          {{ isRegisterMode ? '已有账号？去登录' : '没有账号？去注册' }}
        </el-button>
      </el-form>

      <div v-if="loginError" class="error-msg">{{ loginError }}</div>

    </el-dialog>
  </header>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { Search } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/userStore'

const userStore = useUserStore()
const keyword = ref('')
const isRegisterMode = ref(false)
const loginError = ref('')
const loginLoading = ref(false)

const loginForm = reactive({
  username: '',
  password: '',
  email: ''
})

function doSearch() {
  if (keyword.value.trim()) {
    // window.location.hash 表示是浏览器当前 URL 中的 # (包含)后面的部分
    //encodeURIComponent字符串塞进 URL 参数里，先把里面可能有问题的特殊字符转义一下
    window.location.hash = `#/search?keyword=${encodeURIComponent(keyword.value.trim())}`
  }
}

async function handleLogin() {
  loginError.value = ''
  loginLoading.value = true
  try {
    if (isRegisterMode.value) {//注册
      await userStore.register(loginForm.username, loginForm.password, loginForm.email)
      isRegisterMode.value = false//显示登录界面
      loginError.value = '注册成功，请登录'
    } else {//登录
      await userStore.login(loginForm.username, loginForm.password)
      userStore.closeLogin()
    }
  } catch (e) {
    loginError.value = e.message || '操作失败'
  } finally {//不要一直转圈圈
    loginLoading.value = false
  }
}

function minimize() { window.electron?.minimize() }
function maximize() { window.electron?.maximize() }
function closeWin() { window.electron?.close() }
</script>

<style scoped>
.user-info {
  display: flex; align-items: center; gap: 8px;
  cursor: pointer; -webkit-app-region: no-drag;
}
.user-info:hover .user-name { color: var(--text-primary); }
.user-avatar-img {
  width: 32px; height: 32px; border-radius: 50%; object-fit: cover;
}
</style>
