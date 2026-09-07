import request from '../utils/request'

export function uploadSong(formData) {
  return request.post('/file/upload/song', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
