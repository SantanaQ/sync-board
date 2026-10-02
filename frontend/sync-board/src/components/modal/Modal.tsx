import type { ReactNode } from "react";
import { X } from "lucide-react";

type ModalProps = {
    open: boolean;
    onClose: () => void;
    title: string;
    description?: string;
    children: ReactNode;
    size?: "sm" | "md" | "lg";
};

export default function Modal({
                                  open,
                                  onClose,
                                  title,
                                  description,
                                  children,
                                  size = "md",
                              }: ModalProps) {
    if (!open) {
        return null;
    }

    const sizes = {
        sm: "max-w-md",
        md: "max-w-lg",
        lg: "max-w-2xl",
    };

    return (
        <div className="fixed inset-0 z-[100]">
            {/* Backdrop */}
            <button
                type="button"
                aria-label="Close"
                onClick={onClose}
                className="absolute inset-0 bg-black/30"
            />

            {/* Container */}
            <div className="relative flex min-h-full items-center justify-center p-4">
                <div
                    className={`
                        w-full ${sizes[size]}
                        overflow-hidden
                        rounded-xl
                        border border-border
                        bg-surface
                        shadow-2xl
                    `}
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="modal-title"
                >
                    {/* Header */}
                    <div className="flex items-start justify-between border-b border-border px-5 py-4">
                        <div className="min-w-0">
                            <h2
                                id="modal-title"
                                className="text-base font-semibold text-primary"
                            >
                                {title}
                            </h2>

                            {description && (
                                <p className="mt-1 text-sm text-muted">
                                    {description}
                                </p>
                            )}
                        </div>

                        <button
                            type="button"
                            onClick={onClose}
                            className="icon-button ml-4 shrink-0"
                            aria-label="Close"
                        >
                            <X className="h-4 w-4" />
                        </button>
                    </div>

                    {/* Content */}
                    <div className="p-5">
                        {children}
                    </div>
                </div>
            </div>
        </div>
    );
}