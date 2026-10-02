import {useNavigate} from "react-router-dom";
import {ArrowUpRight, Edit3, Eye, FolderKanban, Trash2} from "lucide-react";
import {formatDateStr} from "../../utils/formatDate.ts";
import DropdownMenu from "../../components/ui/DropdownMenu.tsx";
import {DropdownMenuItem} from "../../components/ui/DropdownMenuItem.tsx";
import type {ProjectListResponse} from "../../api/types.ts";

type ProjectListItemProps = {
    project: ProjectListResponse;
    onEdit: () => void;
    onDelete: () => void;
};

export function ProjectListItem({
                             project,
                             onEdit,
                             onDelete,
                         }: ProjectListItemProps) {
    const navigate = useNavigate();

    return (
        <div className="group flex items-center gap-4 px-5 py-4 transition-colors hover:bg-surface-hover">
            {/* Project Icon */}
            <div className="flex h-11 w-11 shrink-0 items-center justify-center rounded-xl">
                <FolderKanban className="h-5 w-5"/>
            </div>

            {/* Main content */}
            <div className="min-w-0 flex-1">
                <div className="flex items-center gap-3">
                    <h2 className="truncate text-sm font-medium text-primary">
                        {project.name}
                    </h2>

                    <span className="hidden text-xs text-muted sm:inline">
                        {formatDateStr(project.updatedAt)}
                    </span>
                </div>

                <p className="mt-0.5 truncate text-sm text-muted">
                    {project.description}
                </p>
            </div>

            {/* Actions */}
            <div className="flex items-center gap-1">
                <button
                    onClick={() => navigate(`/projects/${project.id}`)}
                    className="icon-button opacity-0 transition-opacity group-hover:opacity-100 focus:opacity-100"
                    aria-label={`open ${project.name}`}
                >
                    <ArrowUpRight className="h-4 w-4"/>
                </button>

                <DropdownMenu>
                    <DropdownMenuItem
                        icon={<Eye className="h-4 w-4"/>}
                        onClick={() =>
                            navigate(`/projects/${project.id}`)
                        }
                    >
                        Open
                    </DropdownMenuItem>

                    <DropdownMenuItem
                        icon={<Edit3 className="h-4 w-4"/>}
                        onClick={onEdit}
                    >
                        Edit
                    </DropdownMenuItem>

                    <div className="my-1 border-t border-border"/>

                    <DropdownMenuItem
                        icon={<Trash2 className="h-4 w-4"/>}
                        destructive
                        onClick={onDelete}
                    >
                        Delete
                    </DropdownMenuItem>
                </DropdownMenu>
            </div>
        </div>
    );
}