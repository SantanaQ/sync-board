
type LoadingSpinnerProps = {
    size?: "sm" | "md" | "lg";
    text?: string;
};

export const LoadingSpinner = ({
    size = "md",
    text,
}: LoadingSpinnerProps) => {
    const sizeClasses = {
        sm: "h-4 w-4 border-2",
        md: "h-6 w-6 border-2",
        lg: "h-10 w-10 border-3",
    };

    return (
        <div className="flex flex-col items-center justify-center gap-3 m-20">
            <div
                className={`
                    ${sizeClasses[size]}
                    animate-spin
                    rounded-full
                    border-border
                    border-t-primary
                `}
                aria-label="Loading"
            />

            {text && (
                <p className="text-sm text-muted">
                    {text}
                </p>
            )}
        </div>
    );
};
