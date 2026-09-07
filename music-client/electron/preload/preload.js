const { contextBridge, ipcRenderer } = require('electron')

contextBridge.exposeInMainWorld('electron', {
  minimize: () => ipcRenderer.invoke('window:minimize'),
  maximize: () => ipcRenderer.invoke('window:maximize'),
  close: () => ipcRenderer.invoke('window:close'),
  isMaximized: () => ipcRenderer.invoke('window:isMaximized'),

  openLyric: () => ipcRenderer.invoke('lyric:open'),
  closeLyric: () => ipcRenderer.invoke('lyric:close'),

  selectMusicFile: () => ipcRenderer.invoke('dialog:selectMusic'),

  sendLyricSync: (data) => ipcRenderer.send('lyric:sync', data),

  onPlayerToggle: (callback) => ipcRenderer.on('player:toggle', callback),
  onPlayerPrev: (callback) => ipcRenderer.on('player:prev', callback),
  onPlayerNext: (callback) => ipcRenderer.on('player:next', callback),

  onLyricUpdate: (callback) => ipcRenderer.on('lyric:update', (event, data) => callback(data))
})
