const { app, BrowserWindow, ipcMain, dialog, Tray, Menu, nativeImage } = require('electron')

// app 控制整个 Electron 应用生命周期
// BrowserWindow 创建窗口
// ipcMain 主进程接收 IPC
// dialog 系统对话框
// Tray 系统托盘
// Menu 托盘菜单
// nativeImage 处理图标

const path = require('path')

let mainWindow = null//主窗口
let lyricWindow = null//桌面歌词窗口
let tray = null//系统托盘

// 开发模式下加载 Vite dev server（支持 HMR 热更新），打包后加载 dist 静态文件
const isDev = !app.isPackaged
const devServerUrl = process.env.VITE_DEV_SERVER_URL || 'http://localhost:5173'

function createMainWindow() {
  mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    minWidth: 900,
    minHeight: 600,
    frame: false,
    webPreferences: {
      preload: path.join(__dirname, '..', 'preload', 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    },
    icon: path.join(__dirname, '..', '..', 'src', 'assets', 'icon.png')
  })

  if (isDev) {
    mainWindow.loadURL(devServerUrl)
  } else {
    mainWindow.loadFile(path.join(__dirname, '..', '..', 'dist', 'index.html'))
  }
}

function createLyricWindow() {
  if (lyricWindow) {
    lyricWindow.focus()
    return
  }
  lyricWindow = new BrowserWindow({
    width: 800,
    height: 120,
    transparent: true,
    frame: false,
    alwaysOnTop: true,
    resizable: false,
    skipTaskbar: true,
    webPreferences: {
      preload: path.join(__dirname, '..', 'preload', 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    }
  })

  const url = isDev
    ? devServerUrl + '#/desktop-lyric'
    : `file://${path.join(__dirname, '..', '..', 'dist', 'index.html')}#/desktop-lyric`

  lyricWindow.loadURL(url)

  lyricWindow.on('closed', () => {
    lyricWindow = null
    // 通知主窗口，让“桌面歌词”按钮取消高亮
    if (mainWindow && !mainWindow.isDestroyed()) {
      mainWindow.webContents.send('lyric:closed')
    }
  })
}

// IPC Handlers
function setupIPC() {
  ipcMain.handle('window:minimize', () => mainWindow?.minimize())
  ipcMain.handle('window:maximize', () => {
    if (mainWindow?.isMaximized()) {
      mainWindow.unmaximize()
    } else {
      mainWindow?.maximize()
    }
  })
  ipcMain.handle('window:close', () => mainWindow?.close())
  ipcMain.handle('window:isMaximized', () => mainWindow?.isMaximized())

  ipcMain.handle('lyric:open', () => createLyricWindow())
  ipcMain.handle('lyric:close', () => lyricWindow?.close())

  ipcMain.handle('dialog:selectMusic', async () => {
    const result = await dialog.showOpenDialog(mainWindow, {
      filters: [{ name: '音乐文件', extensions: ['mp3', 'flac', 'wav', 'ogg'] }],
      properties: ['openFile']
    })
    return result.canceled ? null : result.filePaths[0]
  })
}

function setupTray() {
  const icon = nativeImage.createEmpty()
  tray = new Tray(icon)
  const menu = Menu.buildFromTemplate([
    { label: '播放/暂停', click: () => mainWindow?.webContents.send('player:toggle') },
    { label: '上一首', click: () => mainWindow?.webContents.send('player:prev') },
    { label: '下一首', click: () => mainWindow?.webContents.send('player:next') },
    { type: 'separator' },
    { label: '显示主窗口', click: () => mainWindow?.show() },
    { type: 'separator' },
    { label: '退出', click: () => app.quit() }
  ])
  tray.setToolTip('琉涧音')
  tray.setContextMenu(menu)
  tray.on('click', () => mainWindow?.show())
}

app.whenReady().then(() => {
  setupIPC()
  createMainWindow()
  setupTray()
})

app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit()
  }
})

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createMainWindow()
  }
})

// Send lyric sync data to lyric window
ipcMain.on('lyric:sync', (event, data) => {
  if (lyricWindow && !lyricWindow.isDestroyed()) {
    lyricWindow.webContents.send('lyric:update', data)
  }
})
