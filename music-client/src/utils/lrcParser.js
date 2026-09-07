/**
 * LRC 歌词解析器
 * 将 LRC 格式文本解析为带时间戳的歌词数组
 *
 * 输入:
 *   [00:12.50]第一句歌词
 *   [00:20.00]第二句歌词
 *
 * 输出:
 *   [{ time: 12.5, text: '第一句歌词' }, ...]
 */

export function parseLRC(lrcText) {
  if (!lrcText) return []

  const lines = lrcText.split('\n')
  const lyrics = []

  const timeRegex = /\[(\d{2}):(\d{2})\.(\d{2,3})\]/

  for (const line of lines) {
    const match = line.match(timeRegex)
    if (!match) continue

    const minutes = parseInt(match[1])
    const seconds = parseInt(match[2])
    let milliseconds = parseInt(match[3])
    // 如果毫秒是3位(int)则直接使用，2位需要×10
    if (match[3].length === 2) milliseconds *= 10

    const time = minutes * 60 + seconds + milliseconds / 1000
    const text = line.replace(timeRegex, '').trim()

    if (text) {
      lyrics.push({ time, text })
    }
  }

  return lyrics.sort((a, b) => a.time - b.time)
}

/**
 * 根据当前播放时间查找对应的歌词索引
 */
export function findLyricIndex(lyrics, currentTime) {
  if (!lyrics || lyrics.length === 0) return -1

  for (let i = lyrics.length - 1; i >= 0; i--) {
    if (currentTime >= lyrics[i].time) {
      return i
    }
  }
  return -1
}
