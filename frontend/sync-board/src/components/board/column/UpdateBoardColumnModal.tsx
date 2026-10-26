import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import UpdateModal from "../../modal/UpdateModal";

import { useApiError } from "../../../hooks/useApiError.ts";
import type {BoardColumnResponse} from "../../../api/types.ts";

import {updateColumn} from "../../../api/boardColumnClient.ts";
import {TextInput} from "../../form/TextInput.tsx";

type UpdateBoardColumnModalProps = {
    open: boolean;
    column: BoardColumnResponse;
    onClose: () => void;
    onUpdated?: () => void | Promise<void>;
};

export default function UpdateBoardColumnModal({
                                              open,
                                              column,
                                              onClose,
                                              onUpdated,
                                          }: UpdateBoardColumnModalProps) {
    const { projectId, boardId } = useParams();

    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const[name, setName] = useState(column.name);
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        if (!projectId || !boardId) {
            return;
        }

        try {
            clearErrors();
            setIsLoading(true);

            await updateColumn(projectId, boardId, column.id, {
                name,
            });

            await onUpdated?.();

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

    const handleNameChange = (
        event: React.ChangeEvent<HTMLInputElement>,
    ) => {
        setName(event.target.value);
        clearFieldError("name");
    };


    if (!projectId) {
        return null;
    }

    return (
        <>
            <UpdateModal
                open={open}
                onClose={onClose}
                title="Edit column"
                description="Update board column information."
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
                    placeholder="My column name"
                    onChange={handleNameChange}
                    disabled={isLoading}
                />

            </UpdateModal>
        </>
    );
}