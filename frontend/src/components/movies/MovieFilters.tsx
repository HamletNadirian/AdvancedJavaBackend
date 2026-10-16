import { X } from 'lucide-react'
import type { MovieListRequest, Producer } from '../../types'
import Select from '../ui/Select'

const GENRES = [
  'Action','Adventure','Animation','Comedy','Crime',
  'Documentary','Drama','Fantasy','Horror','Musical',
  'Mystery','Romance','Sci-Fi','Thriller','Western',
]

interface MovieFiltersProps {
  filters: MovieListRequest
  producers: Producer[]
  onChange: (f: Partial<MovieListRequest>) => void
  onReset: () => void
}

export default function MovieFilters({ filters, producers, onChange, onReset }: MovieFiltersProps) {
  const hasFilters = filters.genre || filters.producerId || filters.minYear || filters.maxYear

  return (
    <div className="flex flex-wrap items-end gap-3 rounded-xl bg-white p-4 shadow-sm border border-gray-100">
      <Select
        placeholder="All genres"
        value={filters.genre ?? ''}
        onChange={(e) => onChange({ genre: e.target.value || undefined, page: 0 })}
        options={GENRES.map((g) => ({ value: g, label: g }))}
        className="w-40"
      />

      <Select
        placeholder="All producers"
        value={filters.producerId ?? ''}
        onChange={(e) => onChange({ producerId: e.target.value ? Number(e.target.value) : undefined, page: 0 })}
        options={producers.map((p) => ({ value: p.id, label: p.name }))}
        className="w-44"
      />

      <div className="flex items-end gap-2">
        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">Year from</label>
          <input
            type="number"
            min={1888}
            max={2052}
            placeholder="1888"
            value={filters.minYear ?? ''}
            onChange={(e) => onChange({ minYear: e.target.value ? Number(e.target.value) : undefined, page: 0 })}
            className="w-24 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
        <div className="flex flex-col gap-1">
          <label className="text-xs text-gray-500">Year to</label>
          <input
            type="number"
            min={1888}
            max={2052}
            placeholder="2052"
            value={filters.maxYear ?? ''}
            onChange={(e) => onChange({ maxYear: e.target.value ? Number(e.target.value) : undefined, page: 0 })}
            className="w-24 rounded-lg border border-gray-300 px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-indigo-500"
          />
        </div>
      </div>

      {hasFilters && (
        <button
          onClick={onReset}
          className="flex items-center gap-1.5 rounded-lg px-3 py-2 text-sm text-gray-500 hover:bg-gray-100 transition-colors"
        >
          <X size={14} />
          Clear
        </button>
      )}
    </div>
  )
}
