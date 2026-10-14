import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type { ProjectMemberResponse } from "../../../api/types.ts";
import { removeProjectMember } from "../../../api/projectMemberClient.ts";
import { useApiError } from "../../../hooks/useApiError.ts";
import DeleteModal from "../../modal/DeleteModal.tsx";

type DeleteMemberModalProps = {
    open: boolean;
    member: ProjectMemberResponse;
    onClose: () => void;
    onDeleted?: () => void | Promise<void>;
};

export default function DeleteMemberModal({
    open,
    member,
    onClose,
    onDeleted,
}: DeleteMemberModalProps) {
    const { projectId } = useParams();

    const { clearErrors, handleApiError } = useApiError();
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        if (!projectId) {
            return;
        }

        try {
            clearErrors();
            setIsLoading(true);

            await removeProjectMember(projectId, member.user.id);

            await onDeleted?.();

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

    return (
        <DeleteModal
            open={open}
            onClose={onClose}
            title="Remove project member"
            description={`Are you sure you want to remove ${member.user.displayName} from this project?`}
            onSubmit={handleSubmit}
            loading={isLoading}
            submitLabel="Remove"
        />
    );
}