import type {Column} from "../../pages/board/BoardPage.tsx";
import {MoreHorizontal, Plus} from "lucide-react";
import {KanbanCard} from "./KanbanCard.tsx";

export function KanbanColumn({
                          column,
                      }: {
    column: Column;
}) {
    return (
        <section
            className="
                flex
                w-[280px]
                shrink-0
                flex-col
                rounded-xl
                border border-border
                bg-surface-muted
            "
        >
            {/* Column Header */}
            <header className="flex items-center justify-between px-3 py-3">
                <div className="flex items-center gap-2">
                    <h2 className="text-sm font-semibold text-primary">
                        {column.title}
                    </h2>

                    <span
                        className="
                            flex h-5 min-w-5 items-center justify-center
                            rounded-full
                            bg-surface
                            px-1.5
                            text-[11px] font-medium
                            text-muted
                        "
                    >
                        {column.cards.length}
                    </span>
                </div>

                <button
                    type="button"
                    className="icon-button h-7 w-7"
                    aria-label={`${column.title} Optionen`}
                >
                    <MoreHorizontal className="h-4 w-4" />
                </button>
            </header>

            {/* Cards */}
            <div className="flex flex-1 flex-col gap-2 px-2 pb-2">
                {column.cards.map((card) => (
                    <KanbanCard
                        key={card.id}
                        card={card}
                    />
                ))}

                {/* Add card */}
                <button
                    type="button"
                    className="
                        flex items-center gap-2
                        rounded-lg
                        px-3 py-2.5
                        text-left text-xs font-medium
                        text-muted
                        transition-colors
                        hover:bg-surface
                        hover:text-primary
                    "
                >
                    <Plus className="h-4 w-4" />
                    Karte hinzufügen
                </button>
            </div>
        </section>
    );
}