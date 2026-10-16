import axios from 'axios'
import type { ApiError } from '../types'

const client = axios.create({
  baseURL: '/api',
  headers: { 'Content-Type': 'application/json' },
})

client.interceptors.response.use(
  (res) => res,
  (err) => {
    const data = err.response?.data as ApiError | undefined
    const message = data?.message ?? err.message ?? 'Unknown error'
    return Promise.reject(new Error(message))
  }
)

export default client
