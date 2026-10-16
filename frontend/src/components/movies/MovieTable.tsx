import { Pencil, Trash2, Eye } from 'lucide-react'
import type { MovieListItem } from '../../types'
import { GenreBadge, RatingBadge } from '../ui/Badge'
import Spinner from '../ui/Spinner'

interface MovieTableProps {
  movies: MovieListItem[]
  loading: boolean
  onEdit: (movie: MovieListItem) => void
  onDelete: (movie: MovieListItem) => void
  onView: (movie: MovieListItem) => void
}

export default function MovieTable({ movies, loading, onEdit, onDelete, onView }: MovieTableProps) {
  if (loading) {
    return (
      <div className="flex h-64 items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  if (movies.length === 0) {
    return (
      <div className="flex h-64 flex-col items-center justify-center gap-2 text-gray-400">
        <span className="text-4xl">🎬</span>
        <p className="text-sm">No movies found. Try adjusting your filters.</p>
      </div>
    )
  }

  return (
    <div className="overflow-x-auto">
      <table className="min-w-full divide-y divide-gray-100">
        <thead>
          <tr className="bg-gray-50">
            {['Title', 'Year', 'Genre', 'Producer', 'Rating', 'Actions'].map((h) => (
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
          {movies.map((movie) => (
            <tr key={movie.id} className="table-row-hover group">
              <td className="px-4 py-3">
                <p className="font-medium text-gray-900 text-sm">{movie.title}</p>
                {movie.description && (
                  <p className="text-xs text-gray-400 truncate max-w-xs">{movie.description}</p>
                )}
              </td>
              <td className="px-4 py-3 text-sm text-gray-600">{movie.releaseDate}</td>
              <td className="px-4 py-3">
                <GenreBadge label={movie.genre} />
              </td>
              <td className="px-4 py-3 text-sm text-gray-600">{movie.producerName}</td>
              <td className="px-4 py-3">
                <RatingBadge value={movie.rating} />
              </td>
              <td className="px-4 py-3">
                <div className="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
                  <button
                    onClick={() => onView(movie)}
                    title="View details"
                    className="rounded p-1.5 text-gray-400 hover:bg-indigo-50 hover:text-indigo-600 transition-colors"
                  >
                    <Eye size={15} />
                  </button>
                  <button
                    onClick={() => onEdit(movie)}
                    title="Edit"
                    className="rounded p-1.5 text-gray-400 hover:bg-blue-50 hover:text-blue-600 transition-colors"
                  >
                    <Pencil size={15} />
                  </button>
                  <button
                    onClick={() => onDelete(movie)}
                    title="Delete"
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
