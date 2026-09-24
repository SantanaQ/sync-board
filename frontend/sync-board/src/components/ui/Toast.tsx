import { useEffect } from "react";

export type ToastType = "success" | "error" | "info" | "warning";

export interface ToastData {
    id: string;
    type: ToastType;
    message: string;
    duration?: number;
}

interface ToastProps {
    toast: ToastData;
    onClose: (id: string) => void;
}

export function Toast({ toast, onClose }: ToastProps) {
    const duration = toast.duration ?? 4000;

    useEffect(() => {
        const timeout = window.setTimeout(() => {
            onClose(toast.id);
        }, duration);

        return () => {
            window.clearTimeout(timeout);
        };
    }, [toast.id, duration, onClose]);

    const styles = {
        success: {
            container:
                "border-green-200 bg-green-50 text-green-900 dark:border-green-900 dark:bg-green-950 dark:text-green-100",
            icon: "✓",
        },
        error: {
            container:
                "border-red-200 bg-red-50 text-red-900 dark:border-red-900 dark:bg-red-950 dark:text-red-100",
            icon: "!",
        },
        warning: {
            container:
                "border-yellow-200 bg-yellow-50 text-yellow-900 dark:border-yellow-900 dark:bg-yellow-950 dark:text-yellow-100",
            icon: "!",
        },
        info: {
            container:
                "border-blue-200 bg-blue-50 text-blue-900 dark:border-blue-900 dark:bg-blue-950 dark:text-blue-100",
            icon: "i",
        },
    }[toast.type];

    return (
        <div
            role={toast.type === "error" ? "alert" : "status"}
            className={`
                pointer-events-auto
                flex
                w-full
                max-w-sm
                items-start
                gap-3
                rounded-lg
                border
                p-4
                shadow-lg
                animate-in
                slide-in-from-right-5
                fade-in
                duration-200
                ${styles.container}
            `}
        >
            <div
                className="
                    flex
                    h-6
                    w-6
                    shrink-0
                    items-center
                    justify-center
                    rounded-full
                    bg-black/10
                    text-sm
                    font-bold
                "
                aria-hidden="true"
            >
                {styles.icon}
            </div>

            <p className="flex-1 text-sm font-medium leading-5">
                {toast.message}
            </p>

            <button
                type="button"
                onClick={() => onClose(toast.id)}
                className="
                    shrink-0
                    rounded
                    p-1
                    opacity-60
                    transition
                    hover:opacity-100
                    focus:outline-none
                    focus-visible:ring-2
                    focus-visible:ring-current
                "
                aria-label="Close notification"
            >
                ×
            </button>
        </div>
    );
}