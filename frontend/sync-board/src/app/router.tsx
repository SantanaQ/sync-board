import { BrowserRouter, Routes, Route } from "react-router";
import App from "./App.tsx";
import LoginPage from "../pages/auth/LoginPage.tsx"
import RegisterPage from "../pages/auth/RegisterPage.tsx";
import WelcomePage from "../pages/WelcomePage.tsx";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/app" element={<App />} />
                <Route path="/welcome" element={<WelcomePage />} />
            </Routes>
        </BrowserRouter>
    );
}
