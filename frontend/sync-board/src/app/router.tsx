import { BrowserRouter, Routes, Route } from "react-router";
import App from "./App.tsx";
import LoginPage from "../auth/pages/LoginPage.tsx"
import RegisterPage from "../auth/pages/RegisterPage.tsx";

export default function Router() {
    return (
        <BrowserRouter>
            <Routes>
                <Route path="/" element={<App />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/app" element={<App />} />
            </Routes>
        </BrowserRouter>
    );
}
