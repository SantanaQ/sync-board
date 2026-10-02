import React, {useState} from "react";
import CreateModal from "../modal/CreateModal";
import {TextInput} from "../form/TextInput.tsx";
import {useApiError} from "../../hooks/useApiError.ts";
import {createProject} from "../../api/projectClient.ts";

type CreateProjectModalProps = {
    open: boolean;
    onClose: () => void;
    onCreated?: () => void | Promise<void>;
};

export default function CreateProjectModal({
                                               open,
                                               onClose,
                                               onCreated,
                                           }: CreateProjectModalProps) {
    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();


    const [name, setName] = useState("");
    const [description, setDescription] = useState("");
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await createProject({
                name,
                description,
            });

            await onCreated?.();

            setName("");
            setDescription("");

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

    const handleDescriptionChange = (
        event: React.ChangeEvent<HTMLInputElement>,
    ) => {
        setDescription(event.target.value);
        clearFieldError("description");
    }

    const handleClose = () => {
        if (isLoading) {
            return;
        }

        setName("");
        setDescription("");
        onClose();
    };

    return (
        <CreateModal
            open={open}
            onClose={handleClose}
            title="New Project"
            description="Create a new project to organize your work."
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
                placeholder="My awesome project"
                onChange={handleNameChange}
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