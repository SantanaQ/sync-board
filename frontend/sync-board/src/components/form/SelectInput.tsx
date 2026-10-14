import type { ComponentPropsWithoutRef } from "react";
import { FormField } from "./FormField";
import { inputStyles } from "./inputStyles.ts";

export interface SelectItem {
    label: string;
    value: string | number;
}

interface SelectInputProps extends ComponentPropsWithoutRef<"select"> {
    id: string;
    label: string;
    items: SelectItem[];
    placeholder?: string;
    error?: string;
}

export function SelectInput({
                                id,
                                label,
                                items,
                                placeholder,
                                error,
                                required,
                                className = "",
                                ...props
                            }: SelectInputProps) {
    const errorId = `${id}-error`;

    return (
        <FormField id={id} label={label} error={error} required={required}>
            <select
                id={id}
                aria-invalid={!!error}
                aria-describedby={error ? errorId : undefined}
                className={`
                    ${inputStyles}
                    ${
                    error
                        ? "border-red-500 focus:border-red-500 focus:ring-red-500/20"
                        : ""
                }
                    ${className}
                `}
                {...props}
            >
                {/* Optional placeholder */}
                {placeholder && (
                    <option value="" disabled>
                        {placeholder}
                    </option>
                )}

                {items.map((item) => (
                    <option key={item.value} value={item.value}>
                        {item.label}
                    </option>
                ))}
            </select>
        </FormField>
    );
}