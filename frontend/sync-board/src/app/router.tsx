import { BrowserRouter, Routes, Route } from "react-router";
import App from "./App.tsx";
import LoginPage from "../pages/auth/LoginPage.tsx"
import RegisterPage from "../pages/auth/RegisterPage.tsx";
import WelcomePage from "../pages/WelcomePage.tsx";
import {RequireAuth} from "../auth/components/RequireAuth.tsx";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                {/* Public */}
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />

                {/* Protected */}
                <Route element={<RequireAuth />}>
                    <Route path="/" element={<App />} />
                    <Route path="/app" element={<App />} />
                    <Route path="/welcome" element={<WelcomePage />} />
                </Route>
            </Routes>
        </BrowserRouter>
    );
}
