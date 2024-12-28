import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom"
import MainLayout from "@/components/layout/MainLayout"
import LoginPage from "@/pages/LoginPage"
import DashboardPage from "@/pages/DashboardPage"
import TasksPage from "@/pages/TasksPage"
import { useAuthStore } from "@/stores/authStore"
import { useEffect } from "react"
import { AdminLayout } from '@/components/layout/AdminLayout'
import { UsersPage } from '@/pages/admin/UsersPage'
import { TeamsPage } from '@/pages/admin/TeamsPage'
import { CategoriesPage } from '@/pages/admin/CategoriesPage'
import { TagsPage } from '@/pages/admin/TagsPage'

function App() {
  const { isAuthenticated, token, logout } = useAuthStore()

  // Token yoksa logout yap
  useEffect(() => {
    if (!token && isAuthenticated) {
      logout()
    }
  }, [token, isAuthenticated, logout])

  return (
    <BrowserRouter>
      <Routes>
        <Route 
          path="/login" 
          element={!isAuthenticated ? <LoginPage /> : <Navigate to="/" />} 
        />
        <Route 
          element={isAuthenticated && token ? <MainLayout /> : <Navigate to="/login" />}
        >
          <Route index element={<DashboardPage />} />
          <Route path="tasks" element={<TasksPage />} />
        </Route>
        <Route 
          path="/admin" 
          element={isAuthenticated ? <AdminLayout /> : <Navigate to="/login" />}
        >
          <Route path="users" element={<UsersPage />} />
          <Route path="teams" element={<TeamsPage />} />
          <Route path="categories" element={<CategoriesPage />} />
          <Route path="tags" element={<TagsPage />} />
        </Route>
        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
