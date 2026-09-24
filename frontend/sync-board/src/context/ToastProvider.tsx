import {
    useCallback,
    useMemo,
    useState,
    type ReactNode,
} from "react";
import { createPortal } from "react-dom";

import {
    Toast,
    type ToastData,
    type ToastType,
} from "../components/ui/Toast";
import {ToastContext, type ToastContextValue} from "./ToastContext";

interface ToastProviderProps {
    children: ReactNode;
}

export function ToastProvider({
                                  children,
                              }: ToastProviderProps) {
    const [toasts, setToasts] = useState<ToastData[]>([]);

    const dismissToast = useCallback((id: string) => {
        setToasts(current =>
            current.filter(toast => toast.id !== id)
        );
    }, []);

    const showToast = useCallback(
        (
            message: string,
            type: ToastType = "info",
            duration = 4000
        ) => {
            const id = crypto.randomUUID();

            setToasts(current => [
                ...current,
                {
                    id,
                    message,
                    type,
                    duration,
                },
            ]);
        },
        []
    );

    const showSuccess = useCallback(
        (message: string, duration?: number) =>
            showToast(message, "success", duration),
        [showToast]
    );

    const showError = useCallback(
        (message: string, duration?: number) =>
            showToast(message, "error", duration),
        [showToast]
    );

    const showWarning = useCallback(
        (message: string, duration?: number) =>
            showToast(message, "warning", duration),
        [showToast]
    );

    const showInfo = useCallback(
        (message: string, duration?: number) =>
            showToast(message, "info", duration),
        [showToast]
    );

    const value = useMemo<ToastContextValue>(
        () => ({
            showToast,
            showSuccess,
            showError,
            showWarning,
            showInfo,
            dismissToast,
        }),
        [
            showToast,
            showSuccess,
            showError,
            showWarning,
            showInfo,
            dismissToast,
        ]
    );

    return (
        <ToastContext.Provider value={value}>
            {children}

            {createPortal(
                <div
                    className="
                        pointer-events-none
                        fixed
                        right-4
                        bottom-4
                        z-50
                        flex
                        w-[calc(100%-2rem)]
                        max-w-sm
                        flex-col
                        gap-3
                    "
                    aria-live="polite"
                    aria-atomic="true"
                >
                    {toasts.map(toast => (
                        <Toast
                            key={toast.id}
                            toast={toast}
                            onClose={dismissToast}
                        />
                    ))}
                </div>,
                document.body
            )}
        </ToastContext.Provider>
    );
}