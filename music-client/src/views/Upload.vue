<template>
  <div class="page-upload">
    <div class="upload-header">
      <h2>
        <Icon icon="mdi:cloud-upload" /> 上传歌曲
      </h2>
      <p>支持 MP3 格式，可附带封面图片和 LRC 歌词文件</p>
    </div>

    <div class="upload-layout">
      <!-- 左侧：音频文件选择 -->
      <div class="file-picker music-picker" @click="triggerMusicInput">
        <div class="picker-icon-wrap" :class="{ ready: !!files.music }">
          <Icon :icon="files.music ? 'mdi:file-music' : 'mdi:music-note-plus'" />
        </div>
        <div class="picker-body">
          <template v-if="files.music">
            <span class="picker-title">{{ files.music.name }}</span>
            <span class="picker-size">{{ formatSize(files.music.size) }}
              <template v-if="duration"> · 时长 {{formatDuration(duration) }}</template>
            </span>
          </template>

          <template v-else>
            <span class="picker-title">选择音频文件</span>
            <span class="picker-hint">点击此处选择 MP3 文件</span>
          </template>
        </div>
        <input ref="musicInput" type="file" accept=".mp3" @change="onFileChange('music', $event)" hidden />
      </div>

      <!-- 右侧：歌曲信息 -->
      <div class="form-card">
        <div class="field">
          <label>
            <Icon icon="mdi:music-note" /> 歌曲名 <em>*</em>
          </label>
          <el-input v-model="form.name" maxlength="100" />
        </div>
        <div class="field">
          <label>
            <Icon icon="mdi:account-music" /> 歌手名 <em>*</em>
          </label>
          <el-input v-model="form.artistName" maxlength="50" />
        </div>
        <div class="field">
          <label>
            <Icon icon="mdi:album" /> 专辑名
          </label>
          <el-input v-model="form.albumName" maxlength="50" />
        </div>
      </div>
    </div>

    <!-- 附加文件 -->
    <div class="extra-row">
      <div class="file-picker cover-picker" :class="{ ready: !!files.cover }" @click="triggerCoverInput">
        <div class="cover-preview" v-if="coverPreview">
          <img :src="coverPreview" alt="cover" />
          <div class="cover-overlay">
            <Icon icon="mdi:refresh" /> 更换
          </div>
        </div>
        <div class="picker-icon-wrap" v-else>
          <Icon icon="mdi:image-plus" />
        </div>
        <div class="picker-body">
          <span class="picker-title">{{ files.cover ? files.cover.name : '添加封面图片' }}</span>
          <span class="picker-hint" v-if="!files.cover">JPG / PNG，作为歌曲封面展示</span>
          <span class="picker-size" v-else>{{ formatSize(files.cover.size) }}</span>
        </div>
        <input ref="coverInput" type="file" accept="image/*" @change="onCoverChange" hidden />
      </div>

      <div class="file-picker lyric-picker" :class="{ ready: !!files.lyric }" @click="triggerLyricInput">
        <div class="picker-icon-wrap">
          <Icon :icon="files.lyric ? 'mdi:subtitles' : 'mdi:subtitles-outline'" />
        </div>
        <div class="picker-body">
          <span class="picker-title">{{ files.lyric ? files.lyric.name : '添加歌词文件' }}</span>
          <span class="picker-hint" v-if="!files.lyric">LRC 格式，用于同步歌词展示</span>
          <span class="picker-size" v-else>{{ formatSize(files.lyric.size) }}</span>
        </div>
        <input ref="lyricInput" type="file" accept=".lrc,.txt" @change="onFileChange('lyric', $event)" hidden />
      </div>
    </div>

    <!-- 提交 -->
    <div class="submit-row">
      <el-button v-if="!uploading" type="danger" size="large" class="btn-submit" :disabled="!canSubmit"
        @click="doUpload" :loading="uploading">
        <Icon icon="mdi:cloud-upload" /> 上传歌曲
      </el-button>
      <div v-else class="uploading-bar">
        <div class="uploading-fill"></div>
        <span class="uploading-text">
          <Icon icon="mdi:loading" class="spin" /> 正在上传...
        </span>
      </div>
    </div>

    <!-- 结果 -->
    <div v-if="result" class="result-card">
      <div class="result-header">
        <Icon icon="mdi:check-decagram" />
        <span>上传成功</span>
      </div>
      <div class="result-grid">
        <div class="result-item">
          <span class="result-label">歌曲</span>
          <span class="result-value">{{ result.name }}</span>
        </div>
        <div class="result-item">
          <span class="result-label">歌手</span>
          <span class="result-value">{{ result.artistName }}</span>
        </div>
        <div class="result-item" v-if="result.albumName">
          <span class="result-label">专辑</span>
          <span class="result-value">{{ result.albumName }}</span>
        </div>
      </div>
      <el-button @click="reset">
        <Icon icon="mdi:plus-circle" /> 继续上传
      </el-button>
    </div>

    <!-- 错误 -->
    <el-alert v-if="error" :title="error" type="error" closable show-icon @close="error = ''" class="error-alert" />
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import * as fileAPI from '../api/file'

const form = reactive({ name: '', artistName: '', albumName: '' })
const files = reactive({ music: null, cover: null, lyric: null })
const uploading = ref(false)
const result = ref(null)
const error = ref('')
const coverPreview = ref(null)
const duration = ref(0)

const musicInput = ref(null)
const coverInput = ref(null)
const lyricInput = ref(null)

const canSubmit = ref(false)
function checkCanSubmit() {
  canSubmit.value = !!(form.name.trim() || form.artistName.trim() || files.music)
}

function triggerMusicInput() { musicInput.value?.click() }
function triggerCoverInput() { coverInput.value?.click() }
function triggerLyricInput() { lyricInput.value?.click() }

async function onFileChange(type, e) {
  const f = e.target.files[0]
  if (!f) return
  files[type] = f
  if (type === 'music') {
    duration.value = await readDuration(f)
  }
  checkCanSubmit()
}

function onCoverChange(e) {
  const f = e.target.files[0]
  if (f) {
    files.cover = f
    if (coverPreview.value) URL.revokeObjectURL(coverPreview.value)
    coverPreview.value = URL.createObjectURL(f)
  }
  checkCanSubmit()
}

function formatSize(bytes) {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(1) + ' MB'
}

// 用浏览器原生解码器读取音频时长（秒），兼容 CBR/VBR，比后端解析更准确
function readDuration(file) {
  return new Promise((resolve) => {
    const url = URL.createObjectURL(file)
    const audio = new Audio()
    audio.preload = 'metadata'
    audio.onloadedmetadata = () => {
      const d = audio.duration
      URL.revokeObjectURL(url)
      resolve(Number.isFinite(d) ? Math.round(d) : 0)
    }
    audio.onerror = () => {
      URL.revokeObjectURL(url)
      resolve(0)
    }
    audio.src = url
  })
}

function formatDuration(sec) {
  if (!sec) return '--:--'
  const m = Math.floor(sec / 60)
  const s = sec % 60
  return `${m}:${String(s).padStart(2, '0')}`
}

async function doUpload() {
  if (!files.music && !form.name.trim()) return
  uploading.value = true
  error.value = ''
  result.value = null

  try {
    const fd = new FormData()
    fd.append('name', form.name)
    fd.append('artistName', form.artistName)
    if (form.albumName) fd.append('albumName', form.albumName)
    fd.append('duration', duration.value)
    if (files.music) fd.append('musicFile', files.music)
    if (files.cover) fd.append('coverFile', files.cover)
    if (files.lyric) fd.append('lyricFile', files.lyric)

    result.value = await fileAPI.uploadSong(fd)
  } catch (e) {
    error.value = e.message || '上传失败，请重试'
  } finally {
    uploading.value = false
  }
}

function reset() {
  result.value = null
  form.name = ''
  form.artistName = ''
  form.albumName = ''
  files.music = null
  files.cover = null
  files.lyric = null
  if (coverPreview.value) { URL.revokeObjectURL(coverPreview.value); coverPreview.value = null }
  duration.value = 0
  canSubmit.value = false
}
</script>

<style scoped>
.page-upload {
  max-width: 780px;
}

.upload-header {
  margin-bottom: 28px;
}

.upload-header h2 {
  font-size: 22px;
  margin-bottom: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-header p {
  color: var(--text-muted);
  font-size: 13px;
}

.upload-layout {
  display: flex;
  gap: 20px;
}

.file-picker {
  display: flex;
  align-items: center;
  gap: 16px;
  background: var(--bg-card);
  border: 2px dashed var(--border-color);
  border-radius: 12px;
  padding: 20px 24px;
  cursor: pointer;
  transition: border-color 0.2s, background 0.2s;
}

.file-picker:hover {
  border-color: var(--text-muted);
  background: var(--bg-hover);
}

.file-picker.ready {
  border-color: transparent;
  border-style: solid;
}

.music-picker {
  flex: 1;
  min-width: 240px;
}

.picker-icon-wrap {
  width: 52px;
  height: 52px;
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: var(--text-muted);
  flex-shrink: 0;
  transition: background 0.2s, color 0.2s;
}

.picker-icon-wrap.ready {
  background: rgba(236, 65, 65, 0.12);
  color: var(--accent);
}

.picker-body {
  overflow: hidden;
}

.picker-title {
  display: block;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.picker-hint {
  display: block;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 4px;
}

.picker-size {
  display: block;
  font-size: 12px;
  color: var(--text-muted);
  margin-top: 2px;
  font-family: monospace;
}

.form-card {
  width: 280px;
  flex-shrink: 0;
  background: var(--bg-card);
  border-radius: 12px;
  padding: 20px 24px;
}

.field {
  margin-bottom: 16px;
}

.field:last-child {
  margin-bottom: 0;
}

.field label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-secondary);
  margin-bottom: 6px;
}

.field label em {
  color: var(--accent);
  font-style: normal;
  font-size: 12px;
}

.extra-row {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.extra-row .file-picker {
  flex: 1;
  padding: 16px 20px;
}

.extra-row .picker-icon-wrap {
  width: 44px;
  height: 44px;
  font-size: 22px;
}

.cover-preview {
  width: 44px;
  height: 44px;
  border-radius: 8px;
  overflow: hidden;
  position: relative;
  flex-shrink: 0;
}

.cover-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0, 0, 0, 0.6);
  opacity: 0;
  transition: opacity 0.2s;
  color: #fff;
  font-size: 11px;
}

.cover-picker:hover .cover-overlay {
  opacity: 1;
}

.submit-row {
  margin-top: 24px;
}

.btn-submit {
  width: 100% !important;
  height: 48px;
  font-size: 15px;
}

.uploading-bar {
  width: 100%;
  height: 48px;
  border-radius: 12px;
  background: var(--bg-card);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.uploading-fill {
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 70%;
  background: linear-gradient(90deg, transparent, rgba(236, 65, 65, 0.12), transparent);
  animation: upload-shift 1.5s ease-in-out infinite;
}

@keyframes upload-shift {
  0% {
    left: -70%;
  }

  100% {
    left: 100%;
  }
}

.uploading-text {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--text-secondary);
  font-size: 14px;
}

.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.result-card {
  margin-top: 24px;
  background: var(--bg-card);
  border-radius: 12px;
  padding: 24px;
  border: 1px solid rgba(76, 175, 80, 0.25);
}

.result-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 17px;
  font-weight: 600;
  color: #4caf50;
  margin-bottom: 18px;
}

.result-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px 24px;
  margin-bottom: 16px;
}

.result-item {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.result-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
}

.result-value {
  font-size: 14px;
}

.error-alert {
  margin-top: 16px;
}
</style>
