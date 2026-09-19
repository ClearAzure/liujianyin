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

// 应用图标。打包后仍在 asar 里的 src/assets/ 下（见 package.json 的 build.files）
const appIconPath = path.join(__dirname, '..', '..', 'src', 'assets', 'icon.png')

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
    icon: appIconPath
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
    backgroundColor: '#00000000', // 透明窗在 Windows 上首帧前会显示黑底，显式给全透明
    frame: false,
    alwaysOnTop: true,
    resizable: false,
    skipTaskbar: true,
    show: false, // 先不显示，等首帧渲染好再 show，避免看到未绘制完的窗口
    webPreferences: {
      preload: path.join(__dirname, '..', 'preload', 'preload.js'),
      contextIsolation: true,
      nodeIntegration: false
    }
  })

  lyricWindow.once('ready-to-show', () => {
    if (lyricWindow && !lyricWindow.isDestroyed()) lyricWindow.show()
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
  // 托盘图标必须是真实图片：nativeImage.createEmpty() 得到的是 0×0 空图，
  // Windows 通知区域照样留一个槽，但里面没有任何像素 —— 看起来就是"透明的图标"。
  // Windows 托盘按 16×16 渲染、高 DPI 下要 32×32，这里先用 Skia 缩好再交给系统，
  // 比直接把 354×354 丢给系统缩要清晰得多。
  const icon = nativeImage.createFromPath(appIconPath).resize({ width: 32, height: 32 })
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

// 歌词窗口挂载完成后主动报到，反向通知主窗口推一次当前状态。
// 比主窗口 setTimeout 猜加载时间可靠：早推会丢，晚推会看到空占位。
ipcMain.on('lyric:ready', () => {
  if (mainWindow && !mainWindow.isDestroyed()) {
    mainWindow.webContents.send('lyric:request-sync')
  }
})
