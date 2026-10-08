import type {ProjectListResponse} from "../../api/types.ts";
import {useApiError} from "../../hooks/useApiError.ts";
import {useEffect, useState} from "react";
import DeleteModal from "../modal/DeleteModal.tsx";
import {deleteProject} from "../../api/projectClient.ts";

type DeleteProjectModalProps = {
    open: boolean;
    project: ProjectListResponse;
    onClose: () => void;
    onDeleted?: () => void | Promise<void>;
};


export default function DeleteProjectModal({
                                             open,
                                             project,
                                             onClose,
                                             onDeleted,
                                         }: DeleteProjectModalProps) {

    const { clearErrors, handleApiError } = useApiError();

    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await deleteProject(project.id);

            await onDeleted?.();

            onClose();
        } catch (e) {
            handleApiError(e);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (!open) {
            return;
        }

        clearErrors();
    }, [project, open]);

    return (
        <DeleteModal
            open={open}
            onClose={onClose}
            title="Delete project"
            description="The following action will irrevocably delete the resource and all associated components."
            onSubmit={handleSubmit}
            loading={isLoading}
        />
    )


}