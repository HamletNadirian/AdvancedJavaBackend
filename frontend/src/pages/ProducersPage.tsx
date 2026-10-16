import { useState } from 'react'
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { Plus } from 'lucide-react'
import toast from 'react-hot-toast'
import { producersApi } from '../api/producers'
import type { Producer } from '../types'
import ProducerTable from '../components/producers/ProducerTable'
import ProducerForm from '../components/producers/ProducerForm'
import Modal from '../components/ui/Modal'
import Button from '../components/ui/Button'

export default function ProducersPage() {
  const qc = useQueryClient()

  const [showForm, setShowForm] = useState(false)
  const [editTarget, setEditTarget] = useState<Producer | null>(null)
  const [deleteTarget, setDeleteTarget] = useState<Producer | null>(null)

  const { data: producers = [], isLoading } = useQuery({
    queryKey: ['producers'],
    queryFn: producersApi.list,
  })

  const invalidate = () => qc.invalidateQueries({ queryKey: ['producers'] })

  const createMut = useMutation({
    mutationFn: producersApi.create,
    onSuccess: () => { toast.success('Producer added'); setShowForm(false); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  const updateMut = useMutation({
    mutationFn: ({ id, data }: { id: number; data: Parameters<typeof producersApi.update>[1] }) =>
      producersApi.update(id, data),
    onSuccess: () => { toast.success('Producer updated'); setEditTarget(null); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  const deleteMut = useMutation({
    mutationFn: producersApi.remove,
    onSuccess: () => { toast.success('Producer deleted'); setDeleteTarget(null); invalidate() },
    onError: (e: Error) => toast.error(e.message),
  })

  return (
    <div className="p-8 space-y-5">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Producers</h1>
          <p className="text-sm text-gray-500 mt-0.5">{producers.length} producers total</p>
        </div>
        <Button size="sm" onClick={() => setShowForm(true)}>
          <Plus size={15} /> Add Producer
        </Button>
      </div>

      <div className="rounded-2xl bg-white shadow-sm border border-gray-100 overflow-hidden">
        <ProducerTable
          producers={producers}
          loading={isLoading}
          onEdit={setEditTarget}
          onDelete={setDeleteTarget}
        />
      </div>

      {/* Create */}
      <ProducerForm
        open={showForm}
        onClose={() => setShowForm(false)}
        loading={createMut.isPending}
        onSubmit={(d) => createMut.mutateAsync(d)}
      />

      {/* Edit */}
      {editTarget && (
        <ProducerForm
          open={!!editTarget}
          onClose={() => setEditTarget(null)}
          initial={editTarget}
          loading={updateMut.isPending}
          onSubmit={(d) => updateMut.mutateAsync({ id: editTarget.id, data: d })}
        />
      )}

      {/* Delete confirm */}
      <Modal
        open={!!deleteTarget}
        onClose={() => setDeleteTarget(null)}
        title="Delete Producer"
        maxWidth="sm"
      >
        <p className="text-sm text-gray-600">
          Are you sure you want to delete{' '}
          <span className="font-semibold text-gray-900">{deleteTarget?.name}</span>?
          All associated movies will also be deleted.
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
    </div>
  )
}
