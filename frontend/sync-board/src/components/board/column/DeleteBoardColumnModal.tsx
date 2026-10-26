import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import type {BoardColumnResponse} from "../../../api/types.ts";
import { useApiError } from "../../../hooks/useApiError.ts";
import DeleteModal from "../../modal/DeleteModal.tsx";
import {deleteColumn} from "../../../api/boardColumnClient.ts";

type DeleteBoardColumnModalProps = {
    open: boolean;
    column: BoardColumnResponse;
    onClose: () => void;
    onDeleted?: () => void | Promise<void>;
};

export default function DeleteBoardColumnModal({
                                              open,
                                              column,
                                              onClose,
                                              onDeleted,
                                          }: DeleteBoardColumnModalProps) {
    const { projectId, boardId } = useParams();

    const { clearErrors, handleApiError } = useApiError();
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        if (!projectId || !boardId) {
            return;
        }

        try {
            clearErrors();
            setIsLoading(true);

            await deleteColumn(projectId, boardId, column.id);

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
    }, [column, open]);

    return (
        <DeleteModal
            open={open}
            onClose={onClose}
            title="Delete board column"
            description={`Are you sure you want to permanently delete column '${column.name}' ?`}
            onSubmit={handleSubmit}
            loading={isLoading}
            submitLabel="Delete"
        />
    );
}