import type { ReactNode } from "react";

type DropdownMenuItemProps = {
    children: ReactNode;
    icon?: ReactNode;
    onClick?: () => void;
    destructive?: boolean;
};

export function DropdownMenuItem({
                                     children,
                                     icon,
                                     onClick,
                                     destructive = false,
                                 }: DropdownMenuItemProps) {
    return (
        <button
            type="button"
            onClick={onClick}
            className={`
                flex w-full items-center gap-2
                rounded-md
                px-2.5 py-2
                text-left text-sm
                transition-colors
                ${
                destructive
                    ? "text-red-600 hover:bg-red-50"
                    : "text-secondary hover:bg-surface-muted hover:text-primary"
            }
            `}
        >
            {icon && (
                <span className="shrink-0">
                    {icon}
                </span>
            )}

            <span>{children}</span>
        </button>
    );
}