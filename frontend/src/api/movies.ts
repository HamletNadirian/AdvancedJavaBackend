import client from './client'
import type {
  MovieCreateRequest,
  MovieDetail,
  MovieListRequest,
  MovieListResponse,
  MovieUpdateRequest,
  UploadResponse,
} from '../types'

export const moviesApi = {
  list: (params: MovieListRequest) =>
    client.post<MovieListResponse>('/movies/_list', params).then((r) => r.data),

  getById: (id: number) =>
    client.get<MovieDetail>(`/movies/${id}`).then((r) => r.data),

  create: (data: MovieCreateRequest) =>
    client.post<MovieDetail>('/movies', data).then((r) => r.data),

  update: (id: number, data: MovieUpdateRequest) =>
    client.put<MovieDetail>(`/movies/${id}`, data).then((r) => r.data),

  remove: (id: number) => client.delete(`/movies/${id}`),

  exportCsv: () =>
    client
      .post('/movies/_report', {}, { responseType: 'blob' })
      .then((r) => r.data as Blob),

  importJson: (file: File) => {
    const form = new FormData()
    form.append('file', file)
    return client
      .post<UploadResponse>('/movies/upload', form, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((r) => r.data)
  },
}
