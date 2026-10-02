import {
    Plus,
    Search,
} from "lucide-react";
import { Link, useParams } from "react-router-dom";
import {KanbanColumn} from "../../components/board/KanbanColumn.tsx";

export type Card = {
    id: string;
    title: string;
    description?: string;
    priority?: "low" | "medium" | "high";
    assignee?: string;
};

export type Column = {
    id: string;
    title: string;
    cards: Card[];
};

const columns: Column[] = [
    {
        id: "todo",
        title: "Todo",
        cards: [
            {
                id: "1",
                title: "Landingpage konzipieren",
                description: "Wireframes und erste Struktur erstellen.",
                priority: "high",
                assignee: "JD",
            },
            {
                id: "2",
                title: "Content sammeln",
                priority: "medium",
            },
        ],
    },
    {
        id: "progress",
        title: "In Progress",
        cards: [
            {
                id: "3",
                title: "Navigation entwickeln",
                description: "Responsive Navigation für Desktop und Mobile.",
                priority: "high",
                assignee: "MK",
            },
        ],
    },
    {
        id: "review",
        title: "Review",
        cards: [
            {
                id: "4",
                title: "Design System prüfen",
                priority: "medium",
            },
        ],
    },
    {
        id: "done",
        title: "Done",
        cards: [
            {
                id: "5",
                title: "Projektstruktur erstellen",
                priority: "low",
                assignee: "JD",
            },
        ],
    },
];

export default function BoardPage() {
    const { projectId } = useParams();

    return (
        <div className="flex min-h-screen flex-col bg-app">

            {/* Board Header */}
            <header className="border-b border-border bg-surface">
                <div className="px-4 py-5 sm:px-6 lg:px-8">
                    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">

                        <div>
                            <div className="mb-1 flex items-center gap-2 text-xs text-muted">
                                <Link
                                    to="/projects"
                                    className="hover:text-primary"
                                >
                                    Projects
                                </Link>

                                <span>/</span>

                                <Link
                                    to={`/projects/${projectId}`}
                                    className="hover:text-primary"
                                >
                                    Website Redesign
                                </Link>
                            </div>

                            <h1 className="text-xl font-semibold tracking-tight text-primary">
                                Website Board
                            </h1>

                            <p className="mt-1 text-sm text-muted">
                                Project tasks and progress
                            </p>
                        </div>

                        <div className="flex items-center gap-2">
                            <button className="icon-button">
                                <Search className="h-4 w-4" />
                            </button>

                            <button className="btn-primary">
                                <Plus className="h-4 w-4" />
                                Add card
                            </button>
                        </div>
                    </div>
                </div>
            </header>

            {/* Kanban */}
            <main className="flex-1 overflow-x-auto">
                <div className="flex min-h-full gap-4 p-4 sm:p-6 lg:p-8">
                    {columns.map((column) => (
                        <KanbanColumn
                            key={column.id}
                            column={column}
                        />
                    ))}
                </div>
            </main>
        </div>
    );
}