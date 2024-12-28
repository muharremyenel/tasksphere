import { useEffect, useState } from 'react'
import { useForm } from 'react-hook-form'
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Textarea } from '@/components/ui/textarea'
import { useTeamStore } from '@/stores/teamStore'
import { useUserStore } from '@/stores/userStore'
import { Team } from '@/types/team'
import { Avatar } from '@/components/ui/avatar'
import { X, UserPlus } from 'lucide-react'

interface TeamDialogProps {
  team: Team | null
  open: boolean
  onClose: () => void
}

interface TeamFormData {
  name: string
  description?: string
}

export function TeamDialog({ team, open, onClose }: TeamDialogProps) {
  const { register, handleSubmit, reset } = useForm<TeamFormData>()
  const { createTeam, updateTeam, deleteTeam, addMember, removeMember } = useTeamStore()
  const { users } = useUserStore()
  const [showUserList, setShowUserList] = useState(false)

  useEffect(() => {
    if (team) {
      reset({
        name: team.name,
        description: team.description
      })
    } else {
      reset()
    }
  }, [team, reset])

  const onSubmit = async (data: TeamFormData) => {
    try {
      if (team) {
        await updateTeam(team.id, data)
      } else {
        await createTeam(data)
      }
      onClose()
    } catch (error) {
      console.error('Failed to save team:', error)
    }
  }

  const handleDelete = async () => {
    if (!team) return
    if (window.confirm('Are you sure you want to delete this team?')) {
      try {
        await deleteTeam(team.id)
        onClose()
      } catch (error) {
        console.error('Failed to delete team:', error)
      }
    }
  }

  const handleAddMember = async (userId: number) => {
    if (!team) return
    try {
      await addMember(team.id, userId)
      setShowUserList(false)
    } catch (error) {
      console.error('Failed to add member:', error)
    }
  }

  const handleRemoveMember = async (userId: number) => {
    if (!team) return
    try {
      await removeMember(team.id, userId)
    } catch (error) {
      console.error('Failed to remove member:', error)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onClose}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{team ? 'Edit Team' : 'Create Team'}</DialogTitle>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
          <Input {...register('name')} placeholder="Team Name" />
          <Textarea {...register('description')} placeholder="Description" />
          <Button type="submit">Save</Button>
          {team && (
            <Button type="button" variant="destructive" onClick={handleDelete}>
              Delete Team
            </Button>
          )}
        </form>

        {team && (
          <div className="mt-6 space-y-4">
            <div className="flex justify-between items-center">
              <h3 className="font-medium">Team Members</h3>
              <Button 
                variant="outline" 
                size="sm" 
                onClick={() => setShowUserList(!showUserList)}
              >
                <UserPlus className="w-4 h-4 mr-2" />
                Add Member
              </Button>
            </div>

            {/* Member List */}
            <div className="flex flex-wrap gap-2">
              {team.members.map(member => (
                <div 
                  key={member.id}
                  className="flex items-center gap-2 bg-gray-100 rounded-full px-3 py-1"
                >
                  <Avatar
                    src={`https://avatar.vercel.sh/${member.email}.png`}
                    alt={member.name}
                    className="w-6 h-6"
                  />
                  <span className="text-sm">{member.name}</span>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="p-0 h-auto hover:bg-transparent"
                    onClick={() => handleRemoveMember(member.id)}
                  >
                    <X className="w-4 h-4" />
                  </Button>
                </div>
              ))}
            </div>

            {/* User Selection List */}
            {showUserList && (
              <div className="border rounded-lg p-2 space-y-2">
                {users
                  .filter(user => !team.members.find(m => m.id === user.id))
                  .map(user => (
                    <div
                      key={user.id}
                      className="flex items-center justify-between p-2 hover:bg-gray-50 rounded-lg cursor-pointer"
                      onClick={() => handleAddMember(user.id)}
                    >
                      <div className="flex items-center gap-2">
                        <Avatar
                          src={`https://avatar.vercel.sh/${user.email}.png`}
                          alt={user.name}
                          className="w-6 h-6"
                        />
                        <span>{user.name}</span>
                      </div>
                      <UserPlus className="w-4 h-4" />
                    </div>
                  ))}
              </div>
            )}
          </div>
        )}
      </DialogContent>
    </Dialog>
  )
} 