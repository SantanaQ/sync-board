type UnauthorizedHandler = () => void;

let onUnauthorized: UnauthorizedHandler | null = null;

export const registerUnauthorizedHandler = (
    handler: UnauthorizedHandler,
) => {
    onUnauthorized = handler;

    return () => {
        if (onUnauthorized === handler) {
            onUnauthorized = null;
        }
    };
};

export const handleUnauthorized = () => {
    onUnauthorized?.();
};