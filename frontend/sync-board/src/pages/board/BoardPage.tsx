import {
    Edit3,
    Plus,
    Search, Trash2,
} from "lucide-react";
import {Link, useLocation, useNavigate, useParams} from "react-router-dom";
import {KanbanColumn} from "../../components/board/KanbanColumn.tsx";
import DropdownMenu from "../../components/ui/DropdownMenu.tsx";
import {DropdownMenuItem} from "../../components/ui/DropdownMenuItem.tsx";
import {useEffect, useState} from "react";
import type {BoardResponse} from "../../api/types.ts";
import * as boardClient from "../../api/boardClient.ts";
import UpdateBoardModal from "../../components/board/UpdateBoardModal.tsx";
import DeleteBoardModal from "../../components/board/DeleteBoardModal.tsx";

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
            {
                id: "1",
                title: "Landingpage konzipieren",
                description: "Wireframes und erste Struktur erstellen.",
                priority: "high",
                assignee: "JD",
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
    const { projectId, boardId } = useParams();
    const project = useLocation().state?.parent;
    const navigate = useNavigate();

    const [board, setBoard] = useState<BoardResponse | undefined>();

    const [updateModalOpen, setUpdateModalOpen] = useState<boolean>(false);
    const [deleteModalOpen, setDeleteModalOpen] = useState<boolean>(false);


    const handleDelete = () => {
        setDeleteModalOpen(true);
    };

    useEffect(() => {
        if (!projectId || !boardId) return;

        boardClient
            .getBoard(projectId, boardId)
            .then((board) => setBoard(board));
    }, [projectId, boardId]);

    if (!board) return null;

    return (
        <div className="flex h-screen flex-col overflow-hidden bg-app">

            <header className="shrink-0 border-b border-border bg-surface">
                <div className="px-4 py-5 sm:px-6 lg:px-8">
                    <div className="flex flex-col gap-4 lg:flex-row lg:items-center lg:justify-between">

                        <div>
                            <div className="mb-1 flex items-center gap-2 text-xs text-muted">
                                <Link to="/projects" className="hover:text-primary">
                                    Projects
                                </Link>
                                <span>/</span>
                                <Link to={`/projects/${projectId}`} className="hover:text-primary">
                                    {project?.name}
                                </Link>
                            </div>

                            <h1 className="text-xl font-semibold tracking-tight text-primary">
                                {board.name}
                            </h1>

                            <p className="mt-1 text-sm text-muted">
                                Project tasks and progress
                            </p>
                        </div>

                        <div className="flex items-center gap-2">
                            <button className="icon-button">
                                <Search className="h-4 w-4"/>
                            </button>

                            <button className="btn-primary">
                                <Plus className="h-4 w-4"/>
                                Add column
                            </button>

                            <div className="mt-2">
                                <DropdownMenu>
                                    <DropdownMenuItem
                                        icon={<Edit3 className="h-4 w-4"/>}
                                        onClick={() => setUpdateModalOpen(true)}
                                    >
                                        Edit board
                                    </DropdownMenuItem>
                                    <DropdownMenuItem
                                        icon={<Trash2 className="h-4 w-4"/>}
                                        destructive
                                        onClick={handleDelete}
                                    >
                                        Delete board
                                    </DropdownMenuItem>
                                </DropdownMenu>
                            </div>
                        </div>
                    </div>
                </div>
            </header>

            <main className="flex-1 overflow-x-auto overflow-y-hidden">
                <div className="flex h-full gap-4 p-4 sm:p-6 lg:p-8">
                    {columns.map((column) => (
                        <KanbanColumn
                            key={column.id}
                            column={column}
                        />
                    ))}
                </div>
            </main>

            <UpdateBoardModal
                open={updateModalOpen}
                board={board}
                onClose={() => setUpdateModalOpen(false)}
            />
            <DeleteBoardModal
                open={deleteModalOpen}
                board={board}
                onClose={() => setDeleteModalOpen(false)}
                onDeleted={() => navigate(`/projects/${projectId}`)}
            />

        </div>
    );
}