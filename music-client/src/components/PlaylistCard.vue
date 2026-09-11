<template>
  <div class="playlist-card" :title="name" @click="$emit('click')">
    <div class="pl-card-cover">
      <img v-if="coverUrl" :src="coverUrl" alt="cover" />
      <div v-else class="pl-card-placeholder"><Icon :icon="icon" /></div>
      <div class="pl-card-overlay"><Icon icon="mdi:play" /></div>
    </div>
    <div class="pl-card-name">{{ name }}</div>
    <div class="pl-card-count">{{ count }} 首歌曲</div>
  </div>
</template>

<script setup>
// 歌单卡片：封面 + 名称 + 歌曲数，点击由父组件决定跳转（歌单详情 / 我喜欢的音乐）
defineProps({
  name: { type: String, required: true },
  coverUrl: { type: String, default: '' },
  count: { type: Number, default: 0 },
  icon: { type: String, default: 'mdi:playlist-music' }
})

defineEmits(['click'])
</script>

<style scoped>
.playlist-card {
  background: var(--bg-card); border-radius: 8px;
  padding: 12px; cursor: pointer; transition: background 0.2s;
}
.playlist-card:hover { background: var(--bg-hover); }
.pl-card-cover {
  position: relative; width: 100%; padding-top: 100%;
  border-radius: 6px; overflow: hidden; margin-bottom: 8px;
  background: var(--bg-secondary);
}
.pl-card-cover img {
  position: absolute; inset: 0; width: 100%; height: 100%; object-fit: cover;
}
.pl-card-placeholder {
  position: absolute; inset: 0;
  display: flex; align-items: center; justify-content: center;
  font-size: 40px; color: var(--text-muted);
}
.pl-card-overlay {
  position: absolute; right: 8px; bottom: 8px;
  width: 32px; height: 32px; border-radius: 50%;
  background: var(--accent); color: #fff;
  display: flex; align-items: center; justify-content: center;
  font-size: 14px; opacity: 0; transition: opacity 0.2s;
}
.playlist-card:hover .pl-card-overlay { opacity: 1; }
.pl-card-name {
  font-size: 14px; font-weight: bold; margin-bottom: 4px;
  overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.pl-card-count { font-size: 12px; color: var(--text-secondary); }
</style>
