import type { ReactNode } from "react";

interface FormFieldProps {
    id: string;
    label: string;
    error?: string;
    required?: boolean;
    children: ReactNode;
}

export function FormField({
                              id,
                              label,
                              error,
                              required = false,
                              children,
                          }: FormFieldProps) {
    return (
        <div>
            <label
                htmlFor={id}
                className="
                    mb-2
                    inline-block
                    text-sm
                    font-medium
                    text-slate-900
                    dark:text-slate-50
                "
            >
                {label}

                {required && (
                    <span
                        className="ml-1 text-red-600"
                        aria-hidden="true"
                    >
                        *
                    </span>
                )}
            </label>

            {children}

            {error && (
                <p
                    id={`${id}-error`}
                    className="
                        mt-1.5
                        text-sm
                        text-red-600
                        dark:text-red-400
                        italic
                    "
                >
                    {error}
                </p>
            )}
        </div>
    );
}