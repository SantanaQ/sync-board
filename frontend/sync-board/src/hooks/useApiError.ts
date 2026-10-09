import { useCallback, useState } from "react";
import { ApiError } from "../api/error/ApiError.ts";
import { ErrorCode } from "../api/error/ErrorCode.ts";
import { useToast } from "./useToast";

export function useApiError() {
    const [fieldErrors, setFieldErrors] = useState<
        Record<string, string>
    >({});

    const { showError } = useToast();

    const clearErrors = useCallback(() => {
        setFieldErrors({});
    }, []);

    const clearFieldError = useCallback((field: string) => {
        setFieldErrors(current => {
            if (!(field in current)) {
                return current;
            }

            const next = { ...current };
            delete next[field];

            return next;
        });
    }, []);

    const handleApiError = useCallback(
        (error: unknown) => {
            if (error instanceof ApiError) {
                if (
                    error.code ===
                    ErrorCode.VALIDATION_FAILED
                ) {
                    setFieldErrors(error.errors);

                    return;
                }

                showError(error.message);

                return;
            }

            console.error(error);

            showError(
                "An unexpected error occurred. Please try again."
            );
        },
        [showError]
    );

    return {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    };
}