import React, {useState} from "react";
import CreateModal from "../../modal/CreateModal";
import {useApiError} from "../../../hooks/useApiError.ts";
import {addProjectMember} from "../../../api/projectMemberClient.ts";
import {useParams} from "react-router-dom";
import {UserSearch} from "../../form/UserSearch.tsx";
import {SelectInput} from "../../form/SelectInput.tsx";

type AddMemberModalProps = {
    open: boolean;
    onClose: () => void;
    onCreated?: () => void | Promise<void>;
};

export default function AddMemberModal({
                                               open,
                                               onClose,
                                               onCreated,
                                           }: AddMemberModalProps) {
    const {
        fieldErrors,
        clearErrors,
        clearFieldError,
        handleApiError,
    } = useApiError();

    const { projectId } = useParams();

    if(!projectId) {
        return null;
    }

    const [userId, setUserId] = useState("");
    const [role, setRole] = useState("ADMIN");
    const [isLoading, setIsLoading] = useState(false);

    const handleSubmit = async () => {
        try {
            clearErrors();
            setIsLoading(true);

            await addProjectMember(projectId, {
                userId,
                role,
            });

            await onCreated?.();

            setUserId("");
            setRole("");

            onClose();
        } catch (e) {
            handleApiError(e);
        } finally {
            setIsLoading(false);
        }
    };

    const handleRoleChange = (
        event: React.ChangeEvent<HTMLSelectElement>,
    ) => {
        setRole(event.target.value);
        clearFieldError("role");
    }

    const handleClose = () => {
        if (isLoading) {
            return;
        }

        setUserId("");
        setRole("");
        onClose();
    };

    return (
        <CreateModal
            open={open}
            onClose={handleClose}
            title="Add project member"
            description="Find collaborators for your project."
            onSubmit={handleSubmit}
            loading={isLoading}
            submitLabel="Add"
        >
            <UserSearch
                id="userId"
                label="Search users"
                error={fieldErrors.userId}
                maxSelectedItems={1}
                onUserSelect={(user) => {
                    setUserId(user.id);
                }}
            />


            <SelectInput
                id="role"
                label="Assigned role"
                required
                items={[
                    { label: "Admin", value: "ADMIN" },
                    { label: "Member", value: "MEMBER" },
                    { label: "Viewer", value: "VIEWER" },
                ]}
                value={role}
                onChange={handleRoleChange}
                error={fieldErrors.role}
            />
        </CreateModal>
    );
}