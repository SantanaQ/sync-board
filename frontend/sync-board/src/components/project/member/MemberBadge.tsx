import { useState } from "react";
import ProfileIcon from "../../../assets/ProfileIcon.tsx";
import UpdateMemberModal from "./UpdateMemberModal.tsx";
import type {ProjectMemberResponse} from "../../../api/types.ts";

interface MemberBadgeProps {
    member: ProjectMemberResponse;
    className?: string;
    /** Optional: Wenn übergeben, wird das Modal NICHT intern geöffnet, sondern dieser Callback ausgeführt */
    onClick?: () => void;
    /** Ob das interne Modal überhaupt aktiv sein soll (Standard: true, wenn kein onClick da ist) */
    enableModal?: boolean;
    onUpdated?: () => void | Promise<void>;
}

export function MemberBadge({
                                member,
                                className = "",
                                onClick,
                                enableModal = true,
                                onUpdated = () => {}
                            }: MemberBadgeProps) {
    const [isHovered, setIsHovered] = useState(false);
    const [isModalOpen, setIsModalOpen] = useState(false);

    const handleClick = () => {
        // use external onClick
        if (onClick) {
            onClick();
            return;
        }

        // if update modal enabled open modal
        if (enableModal) {
            setIsModalOpen(true);
        }
    };

    return (
        <>
            <div
                className={`relative inline-block ${className}`}
                onMouseEnter={() => setIsHovered(true)}
                onMouseLeave={() => setIsHovered(false)}
            >
                {/* Badge Indicator */}
                <div
                    onClick={handleClick}
                    className="inline-flex items-center gap-1.5 rounded-full border border-border bg-surface-muted px-2.5 py-1 text-xs font-medium text-secondary transition-colors hover:border-accent/40 hover:bg-accent-soft hover:text-accent cursor-pointer"
                >
                    <span className="h-1.5 w-1.5 rounded-full bg-accent" />
                    <span>{member.user.displayName}</span>
                </div>

                {/* Hover Tooltip Card */}
                {isHovered && (
                    <div className="absolute bottom-full left-1/2 z-50 -translate-x-1/2 pb-2">
                        <div className="card w-56 p-3 shadow-md animate-in fade-in zoom-in-95 duration-150 select-text">
                            <div className="flex items-start gap-3">
                                <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-surface-muted text-muted">
                                    <ProfileIcon className="h-5 w-5" />
                                </div>
                                <div className="min-w-0 flex-1">
                                    <p className="truncate text-xs font-semibold text-primary">
                                        {member.user.displayName}
                                    </p>
                                    <p className="truncate text-[11px] text-muted">
                                        {member.user.email}
                                    </p>
                                </div>
                            </div>
                            <div className="absolute top-full left-1/2 -mt-2 -translate-x-1/2 border-x-4 border-t-4 border-x-transparent border-t-border" />
                        </div>
                    </div>
                )}
            </div>


            <UpdateMemberModal
                open={isModalOpen}
                onClose={() => setIsModalOpen(false)}
                member={member}
                onUpdated={onUpdated}
            />

        </>
    );
}