import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import UpdateModal from "../../modal/UpdateModal";
import DeleteMemberModal from "./DeleteMemberModal";

import { useApiError } from "../../../hooks/useApiError.ts";
import type { ProjectMemberResponse } from "../../../api/types.ts";
import { updateProjectMember } from "../../../api/projectMemberClient.ts";

import { SelectInput } from "../../form/SelectInput.tsx";
import ProfileIcon from "../../../assets/ProfileIcon.tsx";

type UpdateMemberModalProps = {
    open: boolean;
    member: ProjectMemberResponse;
    onClose: () => void;
    onUpdated?: () => void | Promise<void>;
};

export default function UpdateMemberModal({
    open,
    member,
    onClose,
    onUpdated,
}: UpdateMemberModalProps) {
    const { projectId } = useParams();

    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const [role, setRole] = useState(member.role);
    const [isLoading, setIsLoading] = useState(false);
    const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);

    useEffect(() => {
        setRole(member.role);
    }, [member]);

    const handleSubmit = async () => {
        if (!projectId) {
            return;
        }

        try {
            clearErrors();
            setIsLoading(true);

            await updateProjectMember(projectId, member.user.id, {
                role,
            });

            await onUpdated?.();

            onClose();
        } catch (e) {
            handleApiError(e);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (open) {
            clearErrors();
        }
    }, [member, open]);

    const handleRoleChange = (
        event: React.ChangeEvent<HTMLSelectElement>,
    ) => {
        setRole(event.target.value);
        clearFieldError("role");
    };

    const handleMemberDeleted = async () => {
        await onUpdated?.();
        setIsDeleteModalOpen(false);
        onClose();
    };

    if (!projectId) {
        return null;
    }

    return (
        <>
            <UpdateModal
                open={open}
                onClose={onClose}
                title="Edit project member"
                description="Update project member information."
                onSubmit={handleSubmit}
                loading={isLoading}
            >
                <div className="flex items-center gap-3 rounded-xl border border-border bg-surface-muted/50 p-4">
                    <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-full bg-primary/10 text-primary">
                        <ProfileIcon className="h-5 w-5" />
                    </div>

                    <div className="min-w-0 flex-1">
                        <div className="flex flex-wrap items-center gap-2">
                            <p className="truncate text-sm font-semibold text-primary">
                                {member.user.displayName}
                            </p>

                            <span className="shrink-0 rounded-full border border-border bg-surface px-2.5 py-1 text-xs font-medium text-secondary">
                                {member.role}
                            </span>
                        </div>

                        <p className="mt-0.5 truncate text-xs text-muted">
                            {member.user.email}
                        </p>

                        <p className="mt-1 text-[10px] font-medium uppercase tracking-wider text-muted">
                            Project member
                        </p>
                    </div>

                    <div className="shrink-0">
                        <button
                            type="button"
                            className="btn-warning"
                            onClick={() => setIsDeleteModalOpen(true)}
                        >
                            Remove
                        </button>
                    </div>
                </div>

                <SelectInput
                    id="role"
                    label="Assigned role"
                    required
                    items={[
                        { label: "Admin", value: "ADMIN" },
                        { label: "Member", value: "MEMBER" },
                        { label: "Viewer", value: "VIEWER" },
                    ]}
                    value={role}
                    onChange={handleRoleChange}
                    error={fieldErrors.role}
                />
            </UpdateModal>

            <DeleteMemberModal
                open={isDeleteModalOpen}
                member={member}
                onClose={() => setIsDeleteModalOpen(false)}
                onDeleted={handleMemberDeleted}
            />
        </>
    );
}