import React, {useState} from "react";
import CreateModal from "../modal/CreateModal";
import {TextInput} from "../form/TextInput.tsx";
import {useApiError} from "../../hooks/useApiError.ts";
import {createBoard} from "../../api/boardClient.ts";
import {useParams} from "react-router-dom";

type CreateBoardModalProps = {
    open: boolean;
    onClose: () => void;
    onCreated?: () => void | Promise<void>;
};

export default function CreateBoardModal({
                                               open,
                                               onClose,
                                               onCreated,
                                           }: CreateBoardModalProps) {
    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();


    const { projectId } = useParams();
    const [name, setName] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    if(!projectId) {
        return null;
    }

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await createBoard(projectId, {
                name
            });

            await onCreated?.();

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

    const handleClose = () => {
        if (isLoading) {
            return;
        }

        setName("");
        onClose();
    };

    return (
        <CreateModal
            open={open}
            onClose={handleClose}
            title="New Board"
            description="Create a new board to organize your team."
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
                placeholder="Board for my project"
                onChange={handleNameChange}
                disabled={isLoading}
            />

        </CreateModal>
    );
}