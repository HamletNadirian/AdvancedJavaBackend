import { ChevronLeft, ChevronRight } from 'lucide-react'

interface PaginationProps {
  currentPage: number
  totalPages: number
  totalElements: number
  pageSize: number
  onPageChange: (page: number) => void
}

export default function Pagination({
  currentPage,
  totalPages,
  totalElements,
  pageSize,
  onPageChange,
}: PaginationProps) {
  const from = currentPage * pageSize + 1
  const to = Math.min((currentPage + 1) * pageSize, totalElements)

  return (
    <div className="flex items-center justify-between border-t border-gray-200 bg-white px-4 py-3">
      <p className="text-sm text-gray-500">
        Showing <span className="font-medium">{from}</span> –{' '}
        <span className="font-medium">{to}</span> of{' '}
        <span className="font-medium">{totalElements}</span> results
      </p>
      <div className="flex items-center gap-1">
        <button
          onClick={() => onPageChange(currentPage - 1)}
          disabled={currentPage === 0}
          className="rounded-lg p-1.5 text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronLeft size={18} />
        </button>
        {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
          const page = totalPages <= 7
            ? i
            : currentPage < 4
              ? i
              : currentPage > totalPages - 5
                ? totalPages - 7 + i
                : currentPage - 3 + i
          return (
            <button
              key={page}
              onClick={() => onPageChange(page)}
              className={`min-w-[32px] rounded-lg px-2 py-1 text-sm font-medium transition-colors
                ${page === currentPage
                  ? 'bg-indigo-600 text-white'
                  : 'text-gray-600 hover:bg-gray-100'
                }`}
            >
              {page + 1}
            </button>
          )
        })}
        <button
          onClick={() => onPageChange(currentPage + 1)}
          disabled={currentPage >= totalPages - 1}
          className="rounded-lg p-1.5 text-gray-500 hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed transition-colors"
        >
          <ChevronRight size={18} />
        </button>
      </div>
    </div>
  )
}
