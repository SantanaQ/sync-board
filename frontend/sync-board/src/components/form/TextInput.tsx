import type { ComponentPropsWithoutRef } from "react";
import { FormField } from "./FormField";
import {inputStyles} from "./inputStyles.ts";

interface TextInputProps extends ComponentPropsWithoutRef<"input"> {
    id: string;
    label: string;
    error?: string;
}

export function TextInput({
                              id,
                              label,
                              error,
                              required,
                              className = "",
                              ...props
                          }: TextInputProps) {
    const errorId = `${id}-error`;

    return (
        <FormField id={id} label={label} error={error} required={required}>
            <input
                id={id}
                required={required}
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
            />
        </FormField>
    );
}