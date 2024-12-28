import * as React from "react"
import { cn } from "@/lib/utils"

export interface AvatarProps extends React.ImgHTMLAttributes<HTMLImageElement> {
  size?: 'sm' | 'md' | 'lg';
}

export function Avatar({ className, alt, ...props }: AvatarProps) {
  return (
    <img
      className={cn(
        "rounded-full aspect-square object-cover",
        className
      )}
      alt={alt}
      {...props}
    />
  )
} 