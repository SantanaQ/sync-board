import React, {useEffect, useState} from "react";
import UpdateModal from "../modal/UpdateModal";
import {TextInput} from "../form/TextInput.tsx";
import {useApiError} from "../../hooks/useApiError.ts";
import type {BoardListResponse} from "../../api/types.ts";
import {updateBoard} from "../../api/boardClient.ts";
import {useParams} from "react-router-dom";

type UpdateBoardModalProps = {
    open: boolean;
    board: BoardListResponse;
    onClose: () => void;
    onUpdated?: () => void | Promise<void>;
};

export default function UpdateBoardModal({
                                               open,
                                               board,
                                               onClose,
                                               onUpdated,
                                           }: UpdateBoardModalProps) {
    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const { projectId, boardId } = useParams();
    const [name, setName] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    if(!projectId || !boardId) {
        return;
    }

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await updateBoard(projectId, boardId, {
                name
            });

            await onUpdated?.();

            setName("");

            onClose();
        } catch (e) {
            handleApiError(e);
        } finally {
            setIsLoading(false);
        }
    };

    const handleNameChange = (
        event: React.ChangeEvent<HTMLInputElement>,
    ) => {
        setName(event.target.value);
        clearFieldError("name");
    };

    useEffect(() => {
        if (!open) {
            return;
        }

        setName(board.name ?? "");
        clearErrors();
    }, [board, open]);

    return (
        <UpdateModal
            open={open}
            onClose={onClose}
            title="Edit board"
            description="Update board information."
            onSubmit={handleSubmit}
            loading={isLoading}
        >
            <TextInput
                id="name"
                label="Name"
                type="text"
                required
                value={name}
                error={fieldErrors.name}
                placeholder="Name for my board"
                onChange={handleNameChange}
                disabled={isLoading}
            />

        </UpdateModal>
    );
}