import { useState } from 'react'
import { useUserStore } from '@/stores/userStore'
import { Button } from '@/components/ui/button'
import { UserDialog } from '@/components/admin/UserDialog'
import { 
  Table, 
  TableHeader, 
  TableBody, 
  TableRow, 
  TableCell 
} from '@/components/ui/table'
import { User } from '@/types/auth'

export function UsersPage() {
  const { users, loading } = useUserStore()
  const [selectedUser, setSelectedUser] = useState<User | null>(null)
  const [isDialogOpen, setIsDialogOpen] = useState(false)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Users</h1>
        <Button onClick={() => setIsDialogOpen(true)}>
          Add User
        </Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Team</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHeader>
        <TableBody>
          {users.map(user => (
            <TableRow key={user.id}>
              <TableCell>{user.name}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>{user.role}</TableCell>
              <TableCell>{user.team?.name || '-'}</TableCell>
              <TableCell>
                <Button 
                  variant="ghost" 
                  onClick={() => {
                    setSelectedUser(user)
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

      <UserDialog 
        user={selectedUser}
        open={isDialogOpen}
        onClose={() => {
          setIsDialogOpen(false)
          setSelectedUser(null)
        }}
      />
    </div>
  )
} 