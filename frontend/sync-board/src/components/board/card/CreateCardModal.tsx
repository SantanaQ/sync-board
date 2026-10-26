import React, {useState} from "react";
import CreateModal from "../../modal/CreateModal";
import {TextInput} from "../../form/TextInput.tsx";
import {useApiError} from "../../../hooks/useApiError.ts";
import {useParams} from "react-router-dom";
import type {BoardColumnResponse} from "../../../api/types.ts";
import {createCard} from "../../../api/cardClient.ts";

type CreateCardModalProps = {
    open: boolean;
    column: BoardColumnResponse
    onClose: () => void;
    onCreated?: () => void | Promise<void>;
};

export default function CreateCardModal({
                                                   open,
                                                   column,
                                                   onClose,
                                                   onCreated,
                                               }: CreateCardModalProps) {
    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();


    const { projectId, boardId } = useParams();
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    if(!projectId || !boardId) {
        return null;
    }

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await createCard(projectId, boardId, column.id, {
                title,
                description,
            });

            await onCreated?.();

            setTitle("");
            setDescription("");

            onClose();
        } catch (e) {
            handleApiError(e);
        } finally {
            setIsLoading(false);
        }
    };

    const handleTitleChange = (
        event: React.ChangeEvent<HTMLInputElement>,
    ) => {
        setTitle(event.target.value);
        clearFieldError("title");
    };

    const handleDescriptionChange = (
        event: React.ChangeEvent<HTMLInputElement>,
    ) => {
        setDescription(event.target.value);
        clearFieldError("description");
    };

    const handleClose = () => {
        if (isLoading) {
            return;
        }

        setTitle("");
        setDescription("");
        onClose();
    };

    return (
        <CreateModal
            open={open}
            onClose={handleClose}
            title="New Card"
            description="Create a new card representing a task state."
            onSubmit={handleSubmit}
            loading={isLoading}
        >
            <TextInput
                id="title"
                label="Title"
                type="text"
                required
                value={title}
                error={fieldErrors.title}
                placeholder="..."
                onChange={handleTitleChange}
                disabled={isLoading}
            />

            <TextInput
                id="description"
                label="Description"
                type="textarea"
                value={description}
                error={fieldErrors.description}
                placeholder="..."
                onChange={handleDescriptionChange}
                disabled={isLoading}
            />

        </CreateModal>
    );
}