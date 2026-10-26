import type {Card} from "../../../pages/board/BoardPage.tsx";
import {PriorityBadge} from "./PriorityBadge.tsx";

export function KanbanCard({
                        card,
                    }: {
    card: Card;
}) {
    return (
        <article
            className="
                group
                cursor-pointer
                rounded-lg
                border border-border
                bg-surface
                p-3
                shadow-sm
                transition
                hover:border-accent/40
                hover:shadow-md
            "
        >
            <div className="flex items-start justify-between gap-2">
                <h3 className="text-sm font-medium leading-5 text-primary">
                    {card.title}
                </h3>
            </div>

            {card.description && (
                <p className="mt-2 line-clamp-2 text-xs leading-5 text-muted">
                    {card.description}
                </p>
            )}

            <div className="mt-3 flex items-center justify-between gap-2">
                {card.priority && (
                    <PriorityBadge priority={card.priority}/>
                )}

                {card.assignee && (
                    <div
                        className="
                            flex h-6 w-6
                            items-center justify-center
                            rounded-full
                            bg-accent-soft
                            text-[10px]
                            font-semibold
                            text-accent
                        "
                        title={card.assignee}
                    >
                        {card.assignee}
                    </div>
                )}
            </div>
        </article>
    );
}