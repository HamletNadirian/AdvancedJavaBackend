import client from './client'
import type { Producer, ProducerRequest } from '../types'

export const producersApi = {
  list: () => client.get<Producer[]>('/producers').then((r) => r.data),

  getById: (id: number) =>
    client.get<Producer>(`/producers/${id}`).then((r) => r.data),

  create: (data: ProducerRequest) =>
    client.post<Producer>('/producers', data).then((r) => r.data),

  update: (id: number, data: ProducerRequest) =>
    client.put<Producer>(`/producers/${id}`, data).then((r) => r.data),

  remove: (id: number) => client.delete(`/producers/${id}`),
}
