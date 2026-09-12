<template>
  <div class="page-search">

    <div class="search-header">
      <h2>搜索结果：{{ keyword }}</h2>
    </div>

    <MusicList :songs="songs" @deleted="doSearch" />
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import MusicList from '../components/MusicList.vue'
import * as musicAPI from '../api/music'

const route = useRoute()
const keyword = ref(route.query.keyword || '')
const songs = ref([])

async function doSearch() {
  if (keyword.value) {
    songs.value = await musicAPI.search(keyword.value)
  }
}

watch(() => route.query.keyword, (val) => {
  keyword.value = val || ''
  doSearch()
}, { immediate: true })
</script>
