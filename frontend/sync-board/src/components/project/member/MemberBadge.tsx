import { useState } from "react";
import ProfileIcon from "../../../assets/ProfileIcon.tsx";

export interface Member {
    displayName: string;
    email: string;
}

interface MemberBadgeProps {
    member: Member;
    className?: string;
}

export function MemberBadge({ member, className = "" }: MemberBadgeProps) {
    const [isHovered, setIsHovered] = useState(false);

    return (
        <div
            className={`relative inline-block ${className}`}
            onMouseEnter={() => setIsHovered(true)}
            onMouseLeave={() => setIsHovered(false)}
        >
            {/* Badge Indicator */}
            <div className="inline-flex items-center gap-1.5 rounded-full border border-border bg-surface-muted px-2.5 py-1 text-xs font-medium text-secondary transition-colors hover:border-accent/40 hover:bg-accent-soft hover:text-accent">
                <span className="h-1.5 w-1.5 rounded-full bg-accent" />
                <span>{member.displayName}</span>
            </div>

            {/* Hover Tooltip Card */}
            {isHovered && (
                <div className="card absolute bottom-full left-1/2 z-50 mb-2 w-56 -translate-x-1/2 p-3 shadow-md animate-in fade-in zoom-in-95 duration-150">
                    <div className="flex items-start gap-3">
                        {/* Profile Icon */}
                        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-surface-muted text-muted">
                            <ProfileIcon className="h-5 w-5" />
                        </div>

                        {/* Details */}
                        <div className="min-w-0 flex-1">
                            <p className="truncate text-xs font-semibold text-primary">
                                {member.displayName}
                            </p>
                            <p className="truncate text-[11px] text-muted">
                                {member.email}
                            </p>
                        </div>
                    </div>

                    {/* Tooltip Arrow */}
                    <div className="absolute top-full left-1/2 -mt-px -translate-x-1/2 border-x-4 border-t-4 border-x-transparent border-t-border" />
                </div>
            )}
        </div>
    );
}