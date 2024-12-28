import { ReactNode } from "react"
import { useAuthStore } from "@/stores/authStore"

interface RoleGuardProps {
  roles?: string[]
  children: ReactNode
}

export function RoleGuard({ roles, children }: RoleGuardProps) {
  const user = useAuthStore((state) => state.user)

  if (!user) return null

  if (roles && !roles.includes(user.role)) {
    return null
  }

  return <>{children}</>
} 