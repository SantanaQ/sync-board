import React, {useState} from "react";
import CreateModal from "../../modal/CreateModal";
import {TextInput} from "../../form/TextInput.tsx";
import {useApiError} from "../../../hooks/useApiError.ts";
import {useParams} from "react-router-dom";
import {createColumn} from "../../../api/boardColumnClient.ts";

type CreateBoardColumnModalProps = {
    open: boolean;
    onClose: () => void;
    onCreated?: () => void | Promise<void>;
};

export default function CreateBoardColumnModal({
                                             open,
                                             onClose,
                                             onCreated,
                                         }: CreateBoardColumnModalProps) {
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
        return null;
    }

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await createColumn(projectId, boardId, {
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
            title="New Column"
            description="Create a new column to organize your tasks."
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
                placeholder="State for my tasks"
                onChange={handleNameChange}
                disabled={isLoading}
            />

        </CreateModal>
    );
}