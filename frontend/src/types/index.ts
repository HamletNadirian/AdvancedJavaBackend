export interface MovieListItem {
  id: number
  title: string
  releaseDate: number
  producerName: string
  genre: string
  description: string
  rating: number
}

export interface MovieDetail {
  id: number
  title: string
  releaseDate: number
  genre: string
  description: string
  rating: number
  producer: Producer
  createdAt: string
  updatedAt: string
}

export interface MovieCreateRequest {
  title: string
  releaseDate: number
  producerId: number
  genre: string
  description?: string
  rating?: number
}

export type MovieUpdateRequest = MovieCreateRequest

export interface MovieListRequest {
  producerId?: number
  genre?: string
  minYear?: number
  maxYear?: number
  page?: number
  size?: number
}

export interface MovieListResponse {
  list: MovieListItem[]
  totalPages: number
  totalElements: number
  currentPage: number
}

export interface Producer {
  id: number
  name: string
  country: string
}

export interface ProducerRequest {
  name: string
  country?: string
}

export interface UploadResponse {
  message?: string
  imported?: number
  total?: number
  errors?: string[]
}

export interface ApiError {
  code?: string
  message: string
  timestamp?: string
  errors?: Record<string, string>
}
