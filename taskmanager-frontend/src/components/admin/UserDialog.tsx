import { useEffect } from 'react'
import { useForm } from 'react-hook-form'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { useTeamStore } from '@/stores/teamStore'
import { User } from '@/types/auth'

interface UserDialogProps {
  user: User | null
  open: boolean
  onClose: () => void
}

interface UserFormData {
  name: string
  email: string
  password?: string
  role: string
  teamId?: number
}

export function UserDialog({ user, open, onClose }: UserDialogProps) {
  const { register, handleSubmit, reset, setValue } = useForm<UserFormData>()
  const { teams, fetchTeams } = useTeamStore()

  useEffect(() => {
    fetchTeams()
  }, [fetchTeams])

  useEffect(() => {
    if (user) {
      setValue('name', user.name)
      setValue('email', user.email)
      setValue('role', user.role)
      setValue('teamId', user.team?.id)
    } else {
      reset()
    }
  }, [user, setValue, reset])

  const onSubmit = async (data: UserFormData) => {
    try {
      // TODO: Implement user create/update
      onClose()
    } catch (error) {
      console.error('Failed to save user:', error)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{user ? 'Edit User' : 'Add User'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input {...register('name')} placeholder="Name" />
          <Input {...register('email')} type="email" placeholder="Email" />
          {!user && <Input {...register('password')} type="password" placeholder="Password" />}
          
          <Select 
            onValueChange={val => setValue('role', val)} 
            defaultValue={user?.role || 'ROLE_USER'}
          >
            <SelectTrigger>
              <SelectValue placeholder="Select role" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ROLE_USER">User</SelectItem>
              <SelectItem value="ROLE_ADMIN">Admin</SelectItem>
            </SelectContent>
          </Select>

          <Select 
            onValueChange={val => setValue('teamId', Number(val))}
            defaultValue={user?.team?.id?.toString()}
          >
            <SelectTrigger>
              <SelectValue placeholder="Select team" />
            </SelectTrigger>
            <SelectContent>
              {teams.map(team => (
                <SelectItem key={team.id} value={team.id.toString()}>
                  {team.name}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>

          <Button type="submit">Save</Button>
        </form>
      </DialogContent>
    </Dialog>
  )
} 