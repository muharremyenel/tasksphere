import { useState } from 'react'
import { useCategoryStore } from '@/stores/categoryStore'
import { Button } from '@/components/ui/button'
import { CategoryDialog } from '@/components/admin/CategoryDialog'
import { 
  Table, 
  TableHeader, 
  TableBody, 
  TableRow, 
  TableCell 
} from '@/components/ui/table'
import { Category } from '@/types/category'

export function CategoriesPage() {
  const { categories, loading } = useCategoryStore()
  const [selectedCategory, setSelectedCategory] = useState<Category | null>(null)
  const [isDialogOpen, setIsDialogOpen] = useState(false)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Categories</h1>
        <Button onClick={() => setIsDialogOpen(true)}>
          Add Category
        </Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Color</TableCell>
            <TableCell>Tasks</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHeader>
        <TableBody>
          {categories.map(category => (
            <TableRow key={category.id}>
              <TableCell>{category.name}</TableCell>
              <TableCell>
                <div 
                  className="w-6 h-6 rounded-full" 
                  style={{ backgroundColor: category.colorHex }} 
                />
              </TableCell>
              <TableCell>{category.taskCount} tasks</TableCell>
              <TableCell>
                <Button 
                  variant="ghost" 
                  onClick={() => {
                    setSelectedCategory(category)
                    setIsDialogOpen(true)
                  }}
                >
                  Edit
                </Button>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      <CategoryDialog 
        category={selectedCategory}
        open={isDialogOpen}
        onClose={() => {
          setIsDialogOpen(false)
          setSelectedCategory(null)
        }}
      />
    </div>
  )
} 