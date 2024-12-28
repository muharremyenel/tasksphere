import { useState } from 'react'
import { useTeamStore } from '@/stores/teamStore'
import { Button } from '@/components/ui/button'
import { TeamDialog } from '@/components/admin/TeamDialog'
import { 
  Table, 
  TableHeader, 
  TableBody, 
  TableRow, 
  TableCell 
} from '@/components/ui/table'
import { Team } from '@/types/team'

export function TeamsPage() {
  const { teams, loading } = useTeamStore()
  const [selectedTeam, setSelectedTeam] = useState<Team | null>(null)
  const [isDialogOpen, setIsDialogOpen] = useState(false)

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-2xl font-bold">Teams</h1>
        <Button onClick={() => setIsDialogOpen(true)}>
          Add Team
        </Button>
      </div>

      <Table>
        <TableHeader>
          <TableRow>
            <TableCell>Name</TableCell>
            <TableCell>Description</TableCell>
            <TableCell>Members</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHeader>
        <TableBody>
          {teams.map(team => (
            <TableRow key={team.id}>
              <TableCell>{team.name}</TableCell>
              <TableCell>{team.description}</TableCell>
              <TableCell>{team.members.length} members</TableCell>
              <TableCell>
                <Button 
                  variant="ghost" 
                  onClick={() => {
                    setSelectedTeam(team)
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

      <TeamDialog 
        team={selectedTeam}
        open={isDialogOpen}
        onClose={() => {
          setIsDialogOpen(false)
          setSelectedTeam(null)
        }}
      />
    </div>
  )
} 