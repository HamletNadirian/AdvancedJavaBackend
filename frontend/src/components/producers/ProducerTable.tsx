import { Pencil, Trash2 } from 'lucide-react'
import type { Producer } from '../../types'
import Spinner from '../ui/Spinner'

interface ProducerTableProps {
  producers: Producer[]
  loading: boolean
  onEdit: (p: Producer) => void
  onDelete: (p: Producer) => void
}

export default function ProducerTable({ producers, loading, onEdit, onDelete }: ProducerTableProps) {
  if (loading) {
    return (
      <div className="flex h-48 items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  if (producers.length === 0) {
    return (
      <div className="flex h-48 flex-col items-center justify-center gap-2 text-gray-400">
        <span className="text-3xl">🎥</span>
        <p className="text-sm">No producers yet. Add one to get started.</p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-100">
        <thead>
          <tr className="bg-gray-50">
            {['#', 'Name', 'Country', 'Actions'].map((h) => (
              <th
                key={h}
                className="px-4 py-3 text-left text-xs font-semibold uppercase tracking-wider text-gray-500"
              >
                {h}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-gray-100 bg-white">
          {producers.map((p) => (
            <tr key={p.id} className="table-row-hover group">
              <td className="px-4 py-3 text-sm text-gray-400 w-12">{p.id}</td>
              <td className="px-4 py-3 text-sm font-medium text-gray-900">{p.name}</td>
              <td className="px-4 py-3 text-sm text-gray-500">{p.country || '—'}</td>
              <td className="px-4 py-3">
                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button
                    onClick={() => onEdit(p)}
                    className="rounded p-1.5 text-gray-400 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <Pencil size={15} />
                  </button>
                  <button
                    onClick={() => onDelete(p)}
                    className="rounded p-1.5 text-gray-400 hover:bg-red-50 hover:text-red-600 transition-colors"
                  >
                    <Trash2 size={15} />
                  </button>
                </div>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
