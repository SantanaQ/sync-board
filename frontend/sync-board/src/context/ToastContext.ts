import {createContext} from "react";
import type {ToastType} from "../components/ui/Toast.tsx";

export interface ToastContextValue {
    showToast: (
        message: string,
        type?: ToastType,
        duration?: number
    ) => void;

    showSuccess: (message: string, duration?: number) => void;
    showError: (message: string, duration?: number) => void;
    showWarning: (message: string, duration?: number) => void;
    showInfo: (message: string, duration?: number) => void;

    dismissToast: (id: string) => void;
}

export const ToastContext = createContext<
    ToastContextValue | undefined
>(undefined);