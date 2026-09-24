import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Router from './app/router.tsx'
import {AuthProvider} from "./auth/context/AuthProvider.tsx";
import {ToastProvider} from "./context/ToastProvider.tsx";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <AuthProvider>
            <ToastProvider>
                <Router />
            </ToastProvider>
        </AuthProvider>
    </StrictMode>,
)
