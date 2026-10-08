import type {BoardListResponse} from "../../api/types.ts";
import {useApiError} from "../../hooks/useApiError.ts";
import {useParams} from "react-router-dom";
import {useEffect, useState} from "react";
import {deleteBoard} from "../../api/boardClient.ts";
import DeleteModal from "../modal/DeleteModal.tsx";

type DeleteBoardModalProps = {
    open: boolean;
    board: BoardListResponse;
    onClose: () => void;
    onDeleted?: () => void | Promise<void>;
};


export default function DeleteBoardModal({
                                             open,
                                             board,
                                             onClose,
                                             onDeleted,
                                         }: DeleteBoardModalProps) {

    const { clearErrors, handleApiError } = useApiError();

    const { projectId, boardId } = useParams();
    const [isLoading, setIsLoading] = useState(false);

    if(!projectId || !boardId) {
        return;
    }

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await deleteBoard(projectId, boardId);

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
    }, [board, open]);

    return (
        <DeleteModal
            open={open}
            onClose={onClose}
            title="Delete board"
            description="The following action will irrevocably delete the resource and all associated components."
            onSubmit={handleSubmit}
            loading={isLoading}
        />
    )


}