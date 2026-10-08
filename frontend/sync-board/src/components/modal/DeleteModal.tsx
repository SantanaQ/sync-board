import React, { type ReactNode } from "react";
import Modal from "./Modal";

type DeleteModalProps = {
    open: boolean;
    onClose: () => void;

    title: string;
    description?: string;

    children?: ReactNode;

    onSubmit: () => void | Promise<void>;

    submitLabel?: string;
    cancelLabel?: string;

    loading?: boolean;
};

export default function DeleteModal({
                                        open,
                                        onClose,
                                        title,
                                        description,
                                        children,
                                        onSubmit,
                                        submitLabel = "Delete",
                                        cancelLabel = "Cancel",
                                        loading = false,
                                    }: DeleteModalProps) {
    const handleSubmit = async (event: React.SubmitEvent<HTMLFormElement>) => {
        event.preventDefault();

        if (loading) {
            return;
        }


        await onSubmit();
    };

    return (
        <Modal
            open={open}
            onClose={loading ? () => {} : onClose}
            title={title}
            description={description}
        >
            <form onSubmit={handleSubmit}>
                <div className="space-y-5">
                    {children}
                </div>

                <div className=" flex justify-end gap-2 border-border pt-4">
                    <button
                        type="button"
                        onClick={onClose}
                        disabled={loading}
                        className="
                            rounded-lg
                            px-3 py-2
                            text-sm font-medium
                            text-secondary
                            transition-colors
                            hover:bg-surface-muted
                            hover:text-primary
                            disabled:cursor-not-allowed
                            disabled:opacity-50
                        "
                    >
                        {cancelLabel}
                    </button>

                    <button
                        type="submit"
                        disabled={loading}
                        className="
                            btn-warning
                            disabled:cursor-not-allowed
                            disabled:opacity-50
                        "
                    >
                        {loading ? "deleting..." : submitLabel}
                    </button>
                </div>
            </form>
        </Modal>
    );
}