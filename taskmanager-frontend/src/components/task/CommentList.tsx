import { Comment } from '@/types/task'
import { Avatar } from '@/components/ui/avatar'
import { format } from 'date-fns'

interface CommentListProps {
  comments: Comment[];
}

export function CommentList({ comments }: CommentListProps) {
  return (
    <div className="space-y-4">
      {comments.map(comment => (
        <div key={comment.id} className="flex gap-3">
          <Avatar
            src={`https://avatar.vercel.sh/${comment.author.email}.png`}
            alt={comment.author.name}
            className="w-8 h-8"
          />
          <div>
            <div className="flex items-center gap-2">
              <span className="font-medium">{comment.author.name}</span>
              <span className="text-sm text-gray-500">
                {format(new Date(comment.createdAt), 'MMM d, yyyy HH:mm')}
              </span>
            </div>
            <p className="text-gray-700 mt-1">{comment.content}</p>
          </div>
        </div>
      ))}
    </div>
  )
} 