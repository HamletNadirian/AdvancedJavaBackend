import { useState, useRef } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus, Download, Upload } from 'lucide-react'
import toast from 'react-hot-toast'
import { moviesApi } from '../api/movies'
import { producersApi } from '../api/producers'
import type { MovieListItem, MovieListRequest } from '../types'
import MovieTable from '../components/movies/MovieTable'
import MovieFilters from '../components/movies/MovieFilters'
import MovieForm from '../components/movies/MovieForm'
import Pagination from '../components/ui/Pagination'
import Modal from '../components/ui/Modal'
import Button from '../components/ui/Button'
import { GenreBadge, RatingBadge } from '../components/ui/Badge'

const DEFAULT_FILTERS: MovieListRequest = { page: 0, size: 20 }

export default function MoviesPage() {
  const qc = useQueryClient()
  const fileRef = useRef<HTMLInputElement>(null)

  const [filters, setFilters] = useState<MovieListRequest>(DEFAULT_FILTERS)
  const [showForm, setShowForm] = useState(false)
  const [editTarget, setEditTarget] = useState<MovieListItem | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<MovieListItem | null>(null)
  const [viewTarget, setViewTarget] = useState<MovieListItem | null>(null)

  const { data, isLoading } = useQuery({
    queryKey: ['movies', filters],
    queryFn: () => moviesApi.list(filters),
    placeholderData: (prev) => prev,
  })

  const { data: producers = [] } = useQuery({
    queryKey: ['producers'],
    queryFn: producersApi.list,
  })

  const { data: viewDetail, isLoading: viewLoading } = useQuery({
    queryKey: ['movie', viewTarget?.id],
    queryFn: () => moviesApi.getById(viewTarget!.id),
    enabled: !!viewTarget,
  })

  const { data: editDetail } = useQuery({
    queryKey: ['movie-edit', editTarget?.id],
    queryFn: () => moviesApi.getById(editTarget!.id),
    enabled: !!editTarget,
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['movies'] })

  const createMut = useMutation({
    mutationFn: moviesApi.create,
    onSuccess: () => { toast.success('Movie added'); setShowForm(false); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Parameters<typeof moviesApi.update>[1] }) =>
      moviesApi.update(id, data),
    onSuccess: () => { toast.success('Movie updated'); setEditTarget(null); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  const deleteMut = useMutation({
    mutationFn: moviesApi.remove,
    onSuccess: () => { toast.success('Movie deleted'); setDeleteTarget(null); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  const [exporting, setExporting] = useState(false)
  const exportCsv = async () => {
    setExporting(true)
    try {
      const blob = await moviesApi.exportCsv()
      const url = URL.createObjectURL(blob)
      const a = document.createElement('a')
      a.href = url
      a.download = 'movies_report.csv'
      a.click()
      URL.revokeObjectURL(url)
      toast.success('Export complete')
    } catch (e: unknown) {
      toast.error((e as Error).message)
    } finally {
      setExporting(false)
    }
  }

  const [importing, setImporting] = useState(false)
  const importJson = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return
    setImporting(true)
    try {
      const res = await moviesApi.importJson(file)
      toast.success(res.message ?? `Imported ${res.imported ?? '?'} movies`)
      invalidate()
    } catch (err: unknown) {
      toast.error((err as Error).message)
    } finally {
      setImporting(false)
      if (fileRef.current) fileRef.current.value = ''
    }
  }

  const updateFilters = (patch: Partial<MovieListRequest>) =>
    setFilters((prev) => ({ ...prev, ...patch }))

  return (
    <div className="p-8 space-y-5">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Movies</h1>
          <p className="text-sm text-gray-500 mt-0.5">
            {data ? `${data.totalElements} movies total` : 'Loading…'}
          </p>
        </div>
        <div className="flex items-center gap-2">
          <input ref={fileRef} type="file" accept=".json" className="hidden" onChange={importJson} />
          <Button variant="secondary" size="sm" loading={importing} onClick={() => fileRef.current?.click()}>
            <Upload size={15} /> Import JSON
          </Button>
          <Button variant="secondary" size="sm" loading={exporting} onClick={exportCsv}>
            <Download size={15} /> Export CSV
          </Button>
          <Button size="sm" onClick={() => setShowForm(true)}>
            <Plus size={15} /> Add Movie
          </Button>
        </div>
      </div>

      {/* Filters */}
      <MovieFilters
        filters={filters}
        producers={producers}
        onChange={updateFilters}
        onReset={() => setFilters(DEFAULT_FILTERS)}
      />

      {/* Table */}
      <div className="rounded-2xl bg-white shadow-sm border border-gray-100 overflow-hidden">
        <MovieTable
          movies={data?.list ?? []}
          loading={isLoading}
          onEdit={setEditTarget}
          onDelete={setDeleteTarget}
          onView={setViewTarget}
        />
        {data && data.totalPages > 1 && (
          <Pagination
            currentPage={data.currentPage}
            totalPages={data.totalPages}
            totalElements={data.totalElements}
            pageSize={filters.size ?? 20}
            onPageChange={(p) => updateFilters({ page: p })}
          />
        )}
      </div>

      {/* Create form */}
      <MovieForm
        open={showForm}
        onClose={() => setShowForm(false)}
        producers={producers}
        loading={createMut.isPending}
        onSubmit={(d) => createMut.mutateAsync(d)}
      />

      {/* Edit form */}
      {editDetail && (
        <MovieForm
          open={!!editTarget}
          onClose={() => setEditTarget(null)}
          producers={producers}
          initial={editDetail}
          loading={updateMut.isPending}
          onSubmit={(d) => updateMut.mutateAsync({ id: editDetail.id, data: d })}
        />
      )}

      {/* Delete confirm */}
      <Modal
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        title="Delete Movie"
        maxWidth="sm"
      >
        <p className="text-sm text-gray-600">
          Are you sure you want to delete{' '}
          <span className="font-semibold text-gray-900">{deleteTarget?.title}</span>?
          This action cannot be undone.
        </p>
        <div className="flex justify-end gap-2 mt-5">
          <Button variant="secondary" onClick={() => setDeleteTarget(null)}>Cancel</Button>
          <Button
            variant="danger"
            loading={deleteMut.isPending}
            onClick={() => deleteTarget && deleteMut.mutate(deleteTarget.id)}
          >
            Delete
          </Button>
        </div>
      </Modal>

      {/* Detail view */}
      <Modal
        open={!!viewTarget}
        onClose={() => setViewTarget(null)}
        title="Movie Details"
        maxWidth="md"
      >
        {viewLoading ? (
          <div className="flex h-40 items-center justify-center">
            <div className="h-8 w-8 animate-spin rounded-full border-2 border-indigo-600 border-t-transparent" />
          </div>
        ) : viewDetail ? (
          <div className="space-y-4">
            <div className="flex items-start justify-between gap-4">
              <div>
                <h3 className="text-xl font-bold text-gray-900">{viewDetail.title}</h3>
                <p className="text-sm text-gray-500 mt-0.5">{viewDetail.releaseDate}</p>
              </div>
              <RatingBadge value={viewDetail.rating} />
            </div>
            <div className="flex gap-2">
              <GenreBadge label={viewDetail.genre} />
            </div>
            {viewDetail.description && (
              <p className="text-sm text-gray-600 leading-relaxed">{viewDetail.description}</p>
            )}
            <div className="rounded-xl bg-gray-50 p-4 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Producer</span>
                <span className="font-medium text-gray-900">{viewDetail.producer.name}</span>
              </div>
              {viewDetail.producer.country && (
                <div className="flex justify-between text-sm">
                  <span className="text-gray-500">Country</span>
                  <span className="text-gray-700">{viewDetail.producer.country}</span>
                </div>
              )}
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Added</span>
                <span className="text-gray-700">
                  {new Date(viewDetail.createdAt).toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>
        ) : null}
      </Modal>
    </div>
  )
}
