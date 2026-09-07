<template>
  <div class="lyric-container" ref="container">
    <div v-if="!lyrics.length" class="lyric-empty">暂无歌词</div>
    <div v-else class="lyric-list" :style="{ transform: `translateY(${-scrollOffset}px)` }">

      <p v-for="(line, i) in lyrics" :key="i" :class="['lyric-line', { active: i === currentIndex }]"
        :ref="el => { if (el) lineEls[i] = el }">
        <!-- 把<p>元素保存到数组lineEls中 下面是展示-->
        {{ line.text }}
      </p>

    </div>
  </div>
</template>

<script setup>
import { ref, watch, computed } from 'vue'
import { usePlayerStore } from '../stores/playerStore'
import { parseLRC, findLyricIndex } from '../utils/lrcParser'

const props = defineProps({
  lrcText: { type: String, default: '' }
})

const playerStore = usePlayerStore()
const container = ref(null)
const lineEls = ref({})

// 计算歌词和当前歌词索引
const lyrics = computed(() => parseLRC(props.lrcText))
const currentIndex = computed(() => findLyricIndex(lyrics.value, playerStore.currentTime))

const scrollOffset = computed(() => {
  const el = lineEls.value[currentIndex.value]
  if (el) {//计算要移动的距离 让当前歌词居中显示 
    return el.offsetTop - (container.value?.clientHeight || 300) / 2 + el.clientHeight / 2
  }
  return 0
})
</script>
