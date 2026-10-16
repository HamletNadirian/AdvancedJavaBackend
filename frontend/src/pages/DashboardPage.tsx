import { useQuery } from '@tanstack/react-query'
import { Film, Users, Star, Calendar } from 'lucide-react'
import { moviesApi } from '../api/movies'
import { producersApi } from '../api/producers'
import { GenreBadge, RatingBadge } from '../components/ui/Badge'
import Spinner from '../components/ui/Spinner'
import { Link } from 'react-router-dom'

function StatCard({ icon: Icon, label, value, color }: {
  icon: typeof Film; label: string; value: number | string; color: string
}) {
  return (
    <div className="flex items-center gap-4 rounded-2xl bg-white p-5 shadow-sm border border-gray-100">
      <div className={`flex h-12 w-12 items-center justify-center rounded-xl ${color}`}>
        <Icon size={22} className="text-white" />
      </div>
      <div>
        <p className="text-2xl font-bold text-gray-900">{value}</p>
        <p className="text-sm text-gray-500">{label}</p>
      </div>
    </div>
  )
}

export default function DashboardPage() {
  const { data: moviesData, isLoading: moviesLoading } = useQuery({
    queryKey: ['movies-dashboard'],
    queryFn: () => moviesApi.list({ page: 0, size: 5 }),
  })

  const { data: producers, isLoading: producersLoading } = useQuery({
    queryKey: ['producers'],
    queryFn: producersApi.list,
  })

  const { data: allMovies } = useQuery({
    queryKey: ['movies-all-count'],
    queryFn: () => moviesApi.list({ page: 0, size: 1 }),
  })

  const topRated = moviesData?.list
    ? [...moviesData.list].sort((a, b) => b.rating - a.rating).slice(0, 3)
    : []

  const loading = moviesLoading || producersLoading

  if (loading) {
    return (
      <div className="flex h-full items-center justify-center">
        <Spinner size="lg" />
      </div>
    )
  }

  return (
    <div className="p-8 space-y-8">
      <div>
        <h1 className="text-2xl font-bold text-gray-900">Dashboard</h1>
        <p className="text-sm text-gray-500 mt-1">Overview of your movie database</p>
      </div>

      <div className="grid grid-cols-2 gap-4 lg:grid-cols-4">
        <StatCard icon={Film} label="Total Movies" value={allMovies?.totalElements ?? 0} color="bg-indigo-500" />
        <StatCard icon={Users} label="Producers" value={producers?.length ?? 0} color="bg-emerald-500" />
        <StatCard
          icon={Star}
          label="Avg Rating"
          value={
            moviesData?.list?.length
              ? (moviesData.list.reduce((s, m) => s + m.rating, 0) / moviesData.list.length).toFixed(1)
              : '—'
          }
          color="bg-amber-500"
        />
        <StatCard
          icon={Calendar}
          label="Latest Release"
          value={
            moviesData?.list?.length
              ? Math.max(...moviesData.list.map((m) => m.releaseDate))
              : '—'
          }
          color="bg-rose-500"
        />
      </div>

      <div className="grid grid-cols-1 gap-6 lg:grid-cols-2">
        {/* Recent movies */}
        <div className="rounded-2xl bg-white shadow-sm border border-gray-100">
          <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
            <h2 className="font-semibold text-gray-900">Recent Movies</h2>
            <Link to="/movies" className="text-sm text-indigo-600 hover:underline">View all</Link>
          </div>
          <ul className="divide-y divide-gray-100">
            {moviesData?.list?.slice(0, 5).map((m) => (
              <li key={m.id} className="flex items-center justify-between px-5 py-3">
                <div>
                  <p className="text-sm font-medium text-gray-900">{m.title}</p>
                  <p className="text-xs text-gray-400">{m.releaseDate} · {m.producerName}</p>
                </div>
                <div className="flex items-center gap-2">
                  <GenreBadge label={m.genre} />
                  <RatingBadge value={m.rating} />
                </div>
              </li>
            ))}
          </ul>
        </div>

        {/* Top rated */}
        <div className="rounded-2xl bg-white shadow-sm border border-gray-100">
          <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
            <h2 className="font-semibold text-gray-900">Top Rated</h2>
            <Link to="/movies" className="text-sm text-indigo-600 hover:underline">View all</Link>
          </div>
          <ul className="divide-y divide-gray-100">
            {topRated.map((m, i) => (
              <li key={m.id} className="flex items-center gap-4 px-5 py-3">
                <span className={`flex h-7 w-7 items-center justify-center rounded-full text-xs font-bold
                  ${i === 0 ? 'bg-amber-100 text-amber-700' : i === 1 ? 'bg-gray-100 text-gray-600' : 'bg-orange-50 text-orange-600'}`}>
                  {i + 1}
                </span>
                <div className="flex-1 min-w-0">
                  <p className="text-sm font-medium text-gray-900 truncate">{m.title}</p>
                  <p className="text-xs text-gray-400">{m.releaseDate}</p>
                </div>
                <RatingBadge value={m.rating} />
              </li>
            ))}
          </ul>
        </div>

        {/* Producers list */}
        <div className="rounded-2xl bg-white shadow-sm border border-gray-100">
          <div className="flex items-center justify-between border-b border-gray-100 px-5 py-4">
            <h2 className="font-semibold text-gray-900">Producers</h2>
            <Link to="/producers" className="text-sm text-indigo-600 hover:underline">Manage</Link>
          </div>
          <ul className="divide-y divide-gray-100">
            {producers?.slice(0, 6).map((p) => (
              <li key={p.id} className="flex items-center justify-between px-5 py-3">
                <p className="text-sm font-medium text-gray-900">{p.name}</p>
                <span className="text-xs text-gray-400">{p.country || '—'}</span>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  )
}
