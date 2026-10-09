import {Edit3, Plus, Trash2} from "lucide-react";
//import {KanbanCard} from "./KanbanCard.tsx";
import DropdownMenu from "../ui/DropdownMenu.tsx";
import {DropdownMenuItem} from "../ui/DropdownMenuItem.tsx";
import type {BoardColumnResponse} from "../../api/types.ts";

export function KanbanColumn({
                                 column,
                             }: {
    column: BoardColumnResponse;
}) {
    return (
        <section
            className="
                flex
                w-[280px]
                h-full
                max-h-full
                shrink-0
                flex-col
                overflow-hidden
                rounded-xl
                border border-border
                bg-surface-muted
            "
        >
            {/* Column Header */}
            <header className="flex shrink-0 items-center justify-between px-3 py-3">
                <div className="flex items-center gap-2">
                    <h2 className="text-sm font-semibold text-primary">
                        {column.name}
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
                        {/*column.cards.length*/} 12
                    </span>
                </div>

                <DropdownMenu>
                    <DropdownMenuItem
                        icon={<Edit3 className="h-4 w-4"/>}
                        onClick={() => {}}
                    >
                        Edit
                    </DropdownMenuItem>
                    <DropdownMenuItem
                        icon={<Trash2 className="h-4 w-4"/>}
                        destructive
                        onClick={() => {}}
                    >
                        Delete
                    </DropdownMenuItem>
                </DropdownMenu>
            </header>

            {/* Cards Container & Add Button Wrapper */}
            <div className="flex flex-1 flex-col overflow-hidden px-2 pb-2">

                <div className="flex-1 overflow-y-auto space-y-2 pr-1">
                    {/*column.cards.map((card, index) => (
                        <KanbanCard
                            key={`${card.id}-${index}`}
                            card={card}
                        />
                    ))*/}
                </div>

                {/* Add card Button */}
                <button
                    type="button"
                    className="
                        mt-2 flex shrink-0 items-center gap-2
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
                    Add card
                </button>
            </div>
        </section>
    );
}