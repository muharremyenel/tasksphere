import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useTagStore } from '@/stores/tagStore'
import { Tag } from '@/types/tag'

interface TagDialogProps {
  tag: Tag | null
  open: boolean
  onClose: () => void
}

interface TagFormData {
  name: string
  colorHex: string
}

export function TagDialog({ tag, open, onClose }: TagDialogProps) {
  const { register, handleSubmit, reset } = useForm<TagFormData>()
  const { createTag, updateTag, deleteTag } = useTagStore()

  useEffect(() => {
    if (tag) {
      reset({
        name: tag.name,
        colorHex: tag.colorHex
      })
    } else {
      reset({
        name: '',
        colorHex: '#000000'
      })
    }
  }, [tag, reset])

  const onSubmit = async (data: TagFormData) => {
    try {
      if (tag) {
        await updateTag(tag.id, data)
      } else {
        await createTag(data)
      }
      onClose()
    } catch (error) {
      console.error('Failed to save tag:', error)
    }
  }

  const handleDelete = async () => {
    if (!tag) return
    if (window.confirm('Are you sure you want to delete this tag?')) {
      try {
        await deleteTag(tag.id)
        onClose()
      } catch (error) {
        console.error('Failed to delete tag:', error)
      }
    }
  }

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{tag ? 'Edit Tag' : 'Create Tag'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <Input {...register('name')} placeholder="Tag Name" />
          </div>

          <div>
            <label className="block text-sm font-medium mb-1">Color</label>
            <div className="flex items-center gap-2">
              <Input 
                {...register('colorHex')} 
                type="color" 
                className="w-20 h-10 p-1"
              />
              <Input 
                {...register('colorHex')} 
                type="text" 
                placeholder="#000000" 
                className="flex-1"
              />
            </div>
          </div>

          <div className="flex justify-between">
            <Button type="submit">Save</Button>
            {tag && (
              <Button 
                type="button" 
                variant="destructive" 
                onClick={handleDelete}
              >
                Delete
              </Button>
            )}
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
} 