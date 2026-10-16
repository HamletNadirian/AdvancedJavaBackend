const GENRE_COLORS: Record<string, string> = {
  'Action':      'bg-red-100 text-red-700',
  'Adventure':   'bg-yellow-100 text-yellow-700',
  'Animation':   'bg-pink-100 text-pink-700',
  'Comedy':      'bg-green-100 text-green-700',
  'Crime':       'bg-gray-200 text-gray-700',
  'Documentary': 'bg-blue-100 text-blue-700',
  'Drama':       'bg-purple-100 text-purple-700',
  'Fantasy':     'bg-indigo-100 text-indigo-700',
  'Horror':      'bg-red-200 text-red-800',
  'Musical':     'bg-pink-100 text-pink-800',
  'Mystery':     'bg-violet-100 text-violet-700',
  'Romance':     'bg-rose-100 text-rose-700',
  'Sci-Fi':      'bg-cyan-100 text-cyan-700',
  'Thriller':    'bg-orange-100 text-orange-700',
  'Western':     'bg-amber-100 text-amber-700',
}

interface BadgeProps {
  label: string
  className?: string
}

export function GenreBadge({ label }: BadgeProps) {
  const color = GENRE_COLORS[label] ?? 'bg-gray-100 text-gray-600'
  return (
    <span className={`inline-block rounded-full px-2.5 py-0.5 text-xs font-medium ${color}`}>
      {label}
    </span>
  )
}

export function RatingBadge({ value }: { value: number }) {
  const color =
    value >= 8 ? 'bg-emerald-100 text-emerald-700' :
    value >= 6 ? 'bg-yellow-100 text-yellow-700' :
                 'bg-red-100 text-red-600'
  return (
    <span className={`inline-flex items-center gap-1 rounded-full px-2.5 py-0.5 text-xs font-semibold ${color}`}>
      ★ {value.toFixed(1)}
    </span>
  )
}
