import { NavLink, Outlet } from 'react-router-dom'
import { Film, Users, LayoutDashboard, Clapperboard } from 'lucide-react'

const navItems = [
  { to: '/', label: 'Dashboard', icon: LayoutDashboard, end: true },
  { to: '/movies', label: 'Movies', icon: Film },
  { to: '/producers', label: 'Producers', icon: Users },
]

export default function AppLayout() {
  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      {/* Sidebar */}
      <aside className="flex w-60 flex-col bg-gray-900 text-white">
        <div className="flex items-center gap-2.5 px-5 py-5 border-b border-gray-700">
          <Clapperboard className="text-indigo-400" size={24} />
          <span className="text-lg font-bold tracking-tight">CineBase</span>
        </div>
        <nav className="flex-1 space-y-1 px-3 py-4">
          {navItems.map(({ to, label, icon: Icon, end }) => (
            <NavLink
              key={to}
              to={to}
              end={end}
              className={({ isActive }) =>
                `flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors
                ${isActive
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-400 hover:bg-gray-800 hover:text-white'
                }`
              }
            >
              <Icon size={18} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="border-t border-gray-700 px-5 py-3">
          <p className="text-xs text-gray-500">Movie Management System</p>
        </div>
      </aside>

      {/* Main content */}
      <main className="flex flex-1 flex-col overflow-hidden">
        <div className="flex-1 overflow-y-auto">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
