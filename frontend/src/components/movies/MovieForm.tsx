import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Modal from '../ui/Modal'
import Input from '../ui/Input'
import Select from '../ui/Select'
import Button from '../ui/Button'
import type { MovieCreateRequest, MovieDetail, Producer } from '../../types'

const GENRES = [
  'Action','Adventure','Animation','Comedy','Crime',
  'Documentary','Drama','Fantasy','Horror','Musical',
  'Mystery','Romance','Sci-Fi','Thriller','Western',
]

const schema = z.object({
  title: z.string().min(1, 'Title is required').max(255),
  releaseDate: z.coerce.number().int().min(1888).max(2052),
  producerId: z.coerce.number().positive('Producer is required'),
  genre: z.string().min(1, 'Genre is required'),
  description: z.string().max(1000).optional(),
  rating: z.preprocess(
    (v) => (v === '' || v === null || v === undefined ? undefined : Number(v)),
    z.number().min(0).max(10).optional()
  ),
})

type FormValues = z.infer<typeof schema>

interface MovieFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: MovieCreateRequest) => Promise<unknown>
  producers: Producer[]
  initial?: MovieDetail
  loading?: boolean
}

export default function MovieForm({ open, onClose, onSubmit, producers, initial, loading }: MovieFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: initial
      ? {
          title: initial.title,
          releaseDate: initial.releaseDate,
          producerId: initial.producer.id,
          genre: initial.genre,
          description: initial.description ?? '',
          rating: initial.rating ?? undefined,
        }
      : { releaseDate: new Date().getFullYear(), rating: undefined },
  })

  const handleClose = () => { reset(); onClose() }

  const submit = handleSubmit(async (data) => {
    await onSubmit(data as MovieCreateRequest)
    reset()
  })

  return (
    <Modal
      open={open}
      onClose={handleClose}
      title={initial ? 'Edit Movie' : 'Add New Movie'}
      maxWidth="lg"
    >
      <form onSubmit={submit} className="space-y-4">
        <Input
          label="Title *"
          placeholder="e.g. Inception"
          error={errors.title?.message}
          {...register('title')}
        />

        <div className="grid grid-cols-2 gap-4">
          <Input
            label="Release Year *"
            type="number"
            placeholder="2010"
            error={errors.releaseDate?.message}
            {...register('releaseDate')}
          />
          <Input
            label="Rating (0–10)"
            type="number"
            step="0.1"
            placeholder="8.8"
            error={errors.rating?.message}
            {...register('rating')}
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Select
            label="Genre *"
            placeholder="Select genre"
            error={errors.genre?.message}
            options={GENRES.map((g) => ({ value: g, label: g }))}
            {...register('genre')}
          />
          <Select
            label="Producer *"
            placeholder="Select producer"
            error={errors.producerId?.message}
            options={producers.map((p) => ({ value: p.id, label: p.name }))}
            {...register('producerId')}
          />
        </div>

        <div className="flex flex-col gap-1">
          <label className="text-sm font-medium text-gray-700">Description</label>
          <textarea
            rows={3}
            placeholder="Brief plot description…"
            className="rounded-lg border border-gray-300 px-3 py-2 text-sm shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 resize-none"
            {...register('description')}
          />
          {errors.description && (
            <p className="text-xs text-red-600">{errors.description.message}</p>
          )}
        </div>

        <div className="flex justify-end gap-2 pt-2">
          <Button variant="secondary" type="button" onClick={handleClose}>
            Cancel
          </Button>
          <Button type="submit" loading={loading}>
            {initial ? 'Save Changes' : 'Add Movie'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}
