import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { z } from 'zod'
import Modal from '../ui/Modal'
import Input from '../ui/Input'
import Button from '../ui/Button'
import type { Producer, ProducerRequest } from '../../types'

const schema = z.object({
  name: z.string().min(1, 'Name is required').max(100),
  country: z.string().max(100).optional(),
})

type FormValues = z.infer<typeof schema>

interface ProducerFormProps {
  open: boolean
  onClose: () => void
  onSubmit: (data: ProducerRequest) => Promise<unknown>
  initial?: Producer
  loading?: boolean
}

export default function ProducerForm({ open, onClose, onSubmit, initial, loading }: ProducerFormProps) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: initial
      ? { name: initial.name, country: initial.country ?? '' }
      : {},
  })

  const handleClose = () => { reset(); onClose() }

  const submit = handleSubmit(async (data) => {
    await onSubmit(data)
    reset()
  })

  return (
    <Modal
      open={open}
      onClose={handleClose}
      title={initial ? 'Edit Producer' : 'Add Producer'}
    >
      <form onSubmit={submit} className="space-y-4">
        <Input
          label="Name *"
          placeholder="e.g. Christopher Nolan"
          error={errors.name?.message}
          {...register('name')}
        />
        <Input
          label="Country"
          placeholder="e.g. USA"
          error={errors.country?.message}
          {...register('country')}
        />
        <div className="flex justify-end gap-2 pt-2">
          <Button variant="secondary" type="button" onClick={handleClose}>
            Cancel
          </Button>
          <Button type="submit" loading={loading}>
            {initial ? 'Save Changes' : 'Add Producer'}
          </Button>
        </div>
      </form>
    </Modal>
  )
}
