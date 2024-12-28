# TaskSphere Frontend

TaskSphere is a modern task management application built with React and TypeScript. This repository contains the frontend codebase of the application.

## Tech Stack

### Core
- **React 18** - A JavaScript library for building user interfaces
- **TypeScript** - JavaScript with syntax for types
- **Vite** - Next Generation Frontend Tooling

### Routing & State Management
- **React Router (v6)** - Declarative routing for React applications
- **Zustand** - A small, fast and scalable state management solution

### API Integration
- **Axios** - Promise based HTTP client
- **TanStack Query** - Powerful asynchronous state management for API data

### Styling & UI
- **Tailwind CSS** - A utility-first CSS framework
- **Radix UI Icons** - A crisp set of 15×15 icons
- **Shadcn/ui** (planned) - Re-usable components built with Radix UI and Tailwind CSS

### Form Handling
- **React Hook Form** - Performant, flexible and extensible forms
- **Zod** - TypeScript-first schema validation
- **@hookform/resolvers** - Resolvers for React Hook Form

## Project Structure

src/
├── components/ # Reusable UI components
├── pages/ # Page components
├── features/ # Feature-specific components and logic
├── hooks/ # Custom React hooks
├── services/ # API services
├── stores/ # Zustand stores
├── types/ # TypeScript type definitions
└── utils/ # Utility functions

## Getting Started

1. **Prerequisites**
   - Node.js (v16 or higher)
   - npm (v7 or higher)

2. **Installation**
   ```bash
   # Clone the repository
   git clone [repository-url]

   # Navigate to project directory
   cd taskmanager-frontend

   # Install dependencies
   npm install
   ```

3. **Development**
   ```bash
   # Start development server
   npm run dev
   ```
   The application will be available at `http://localhost:5173`

4. **Build**
   ```bash
   # Create production build
   npm run build
   ```

## Features

- **Authentication**
  - User login/registration
  - JWT-based authentication

- **Task Management**
  - Create, read, update, delete tasks
  - Assign tasks to users
  - Add collaborators
  - Task categorization and tagging
  - Task status management

- **Categories & Tags**
  - Organize tasks with categories
  - Tag-based filtering
  - Color coding

- **Notifications**
  - Real-time notifications
  - Task assignments
  - Status updates
  - Comments

## API Integration

The frontend communicates with the TaskSphere backend API running on `http://localhost:8080`. Make sure the backend server is running before starting the frontend application.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details
