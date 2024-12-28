export default function TeamPage() {
  const [team, setTeam] = useState<Team | null>(null);
  const [members, setMembers] = useState<User[]>([]);
  const user = useAuthStore((state) => state.user);
  const isAdmin = user?.role === 'ROLE_ADMIN';

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-6">
        <h1 className="text-2xl font-bold">Team</h1>
        {isAdmin && (
          <Button onClick={() => setIsCreateTeamOpen(true)}>
            <RiGroupAddLine className="mr-2 h-4 w-4" />
            New Team
          </Button>
        )}
      </div>

      {/* Team Info */}
      {team && (
        <div className="mb-8">
          <h2 className="text-xl font-semibold">{team.name}</h2>
          <p className="text-gray-600">{team.description}</p>
        </div>
      )}

      {/* Team Members */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        {members.map(member => (
          <div key={member.id} className="p-4 border rounded-lg">
            <h3 className="font-medium">{member.name}</h3>
            <p className="text-sm text-gray-500">{member.email}</p>
          </div>
        ))}
      </div>
    </div>
  )
} 