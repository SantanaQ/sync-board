import type { ButtonHTMLAttributes, ReactNode } from "react";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    isLoading?: boolean;
    loadingText?: ReactNode;
    children: ReactNode;
}

export function FormButton({
                           isLoading = false,
                           loadingText,
                           children,
                           disabled,
                           className = "",
                           ...props
                       }: ButtonProps) {
    return (
        <button
            disabled={disabled || isLoading}
            className={`
                flex w-full items-center justify-center rounded-md border
                border-blue-600 bg-blue-600 px-3.5 py-2 text-sm font-semibold
                tracking-wide text-white transition-all hover:bg-blue-700
                focus:outline-none focus-visible:ring-2 focus-visible:ring-blue-500
                disabled:cursor-not-allowed disabled:opacity-60
                ${className}
            `}
            {...props}
        >
            {isLoading ? (
                <>
                    <span
                        className="
                            mr-2 h-4 w-4 animate-spin rounded-full
                            border-2 border-white/30 border-t-white
                        "
                        aria-hidden="true"
                    />
                    {loadingText ?? children}
                </>
            ) : (
                children
            )}
        </button>
    );
}