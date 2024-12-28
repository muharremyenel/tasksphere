import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { useCategoryStore } from '@/stores/categoryStore'
import { Category } from '@/types/category'

interface CategoryDialogProps {
  category: Category | null
  open: boolean
  onClose: () => void
}

interface CategoryFormData {
  name: string
  colorHex: string
}

export function CategoryDialog({ category, open, onClose }: CategoryDialogProps) {
  const { register, handleSubmit, reset } = useForm<CategoryFormData>()
  const { createCategory, updateCategory, deleteCategory } = useCategoryStore()

  useEffect(() => {
    if (category) {
      reset({
        name: category.name,
        colorHex: category.colorHex
      })
    } else {
      reset({
        name: '',
        colorHex: '#000000'
      })
    }
  }, [category, reset])

  const onSubmit = async (data: CategoryFormData) => {
    try {
      if (category) {
        await updateCategory(category.id, data)
      } else {
        await createCategory(data)
      }
      onClose()
    } catch (error) {
      console.error('Failed to save category:', error)
    }
  }

  const handleDelete = async () => {
    if (!category) return
    if (window.confirm('Are you sure you want to delete this category?')) {
      try {
        await deleteCategory(category.id)
        onClose()
      } catch (error) {
        console.error('Failed to delete category:', error)
      }
    }
  }

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{category ? 'Edit Category' : 'Create Category'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <div>
            <Input {...register('name')} placeholder="Category Name" />
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
            {category && (
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