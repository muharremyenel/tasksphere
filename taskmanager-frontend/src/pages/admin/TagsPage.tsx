import { useState } from 'react'
import { useTagStore } from '@/stores/tagStore'
import { Button } from '@/components/ui/button'
import { TagDialog } from '@/components/admin/TagDialog'
import { 
  Table, 
  TableHeader, 
  TableBody, 
  TableRow, 
  TableCell 
} from '@/components/ui/table'
import { Tag } from '@/types/tag'

export function TagsPage() {
  const { tags, loading } = useTagStore()
  const [selectedTag, setSelectedTag] = useState<Tag | null>(null)
  const [isDialogOpen, setIsDialogOpen] = useState(false)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Tags</h1>
        <Button onClick={() => setIsDialogOpen(true)}>
          Add Tag
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
          {tags.map(tag => (
            <TableRow key={tag.id}>
              <TableCell>{tag.name}</TableCell>
              <TableCell>
                <div 
                  className="w-6 h-6 rounded-full" 
                  style={{ backgroundColor: tag.colorHex }} 
                />
              </TableCell>
              <TableCell>{tag.usageCount} tasks</TableCell>
              <TableCell>
                <Button 
                  variant="ghost" 
                  onClick={() => {
                    setSelectedTag(tag)
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

      <TagDialog 
        tag={selectedTag}
        open={isDialogOpen}
        onClose={() => {
          setIsDialogOpen(false)
          setSelectedTag(null)
        }}
      />
    </div>
  )
} 