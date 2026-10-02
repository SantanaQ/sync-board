import React, {useEffect, useState} from "react";
import UpdateModal from "../modal/UpdateModal";
import {TextInput} from "../form/TextInput.tsx";
import {useApiError} from "../../hooks/useApiError.ts";
import {updateProject} from "../../api/projectClient.ts";
import type {ProjectListResponse} from "../../api/types.ts";

type UpdateProjectModalProps = {
    open: boolean;
    project: ProjectListResponse;
    onClose: () => void;
    onUpdated?: () => void | Promise<void>;
};

export default function UpdateProjectModal({
                                               open,
                                               project,
                                               onClose,
                                               onUpdated,
                                           }: UpdateProjectModalProps) {
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

            await updateProject(project.id, {
                name,
                description,
            });

            await onUpdated?.();

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

    useEffect(() => {
        if (!open) {
            return;
        }

        setName(project.name ?? "");
        setDescription(project.description ?? "");
        clearErrors();
    }, [project, open]);

    return (
        <UpdateModal
            open={open}
            onClose={onClose}
            title="Edit project"
            description="Update project information."
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
                type="text"
                value={description}
                error={fieldErrors.description}
                placeholder="..."
                onChange={handleDescriptionChange}
                disabled={isLoading}
            />
        </UpdateModal>
    );
}