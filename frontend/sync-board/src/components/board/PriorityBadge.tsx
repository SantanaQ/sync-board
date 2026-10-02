import type {Card} from "../../pages/board/BoardPage.tsx";

export function PriorityBadge({
                           priority,
                       }: {
    priority: Card["priority"];
}) {
    if (!priority) {
        return null;
    }

    const labels = {
        low: "Niedrig",
        medium: "Mittel",
        high: "Hoch",
    };

    return (
        <span
            className="
                rounded-md
                bg-surface-muted
                px-2 py-1
                text-[10px]
                font-medium
                text-secondary
            "
        >
            {labels[priority]}
        </span>
    );
}