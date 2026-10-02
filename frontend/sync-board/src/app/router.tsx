import { BrowserRouter, Routes, Route } from "react-router";
import App from "./App.tsx";
import LoginPage from "../pages/auth/LoginPage.tsx"
import RegisterPage from "../pages/auth/RegisterPage.tsx";
import {RequireAuth} from "../auth/components/RequireAuth.tsx";
import Dashboard from "../pages/Dashboard.tsx";
import ProjectListPage from "../pages/list/ProjectListPage.tsx";
import AppLayout from "../components/layout/AppLayout.tsx";
import ProjectDetailPage from "../pages/detail/ProjectDetailPage.tsx";
import BoardPage from "../pages/board/BoardPage.tsx";
import {Navigate} from "react-router-dom";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Public */}
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Protected */}
                <Route element={<RequireAuth />}>
                    <Route element={<AppLayout />}>
                        <Route path="/" element={<Navigate to="/dashboard" replace={true} />} />
                        <Route path="/app" element={<App />} />
                        <Route path="/dashboard" element={<Dashboard />} />
                        <Route path="/settings" element={<Dashboard />} />
                        <Route path="/projects" element={<ProjectListPage />} />
                        <Route path="/projects/:projectId" element={<ProjectDetailPage />} />
                        <Route path="/projects/:projectId/boards/:boardId" element={<BoardPage />} />
                    </Route>
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
