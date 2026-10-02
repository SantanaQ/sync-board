import {useState} from "react";

import {ListPage} from "./ListPage";
import {getProjects, deleteProject} from "../../api/projectClient.ts";
import type {ProjectListResponse} from "../../api/types.ts";
import {useApiError} from "../../hooks/useApiError.ts";
import {useCrudList} from "../../hooks/useCrudList.ts";

import CreateProjectModal from "../../components/project/CreateProjectModal.tsx";
import UpdateProjectModal from "../../components/project/UpdateProjectModal.tsx";

import {ProjectListItem} from "./ProjectListItem.tsx";

export default function ProjectListPage() {
    const {handleApiError} = useApiError();

    const {
        items: projects,
        isLoading,
        refresh,
        remove,
    } = useCrudList<ProjectListResponse>({
        fetchItems: getProjects,
        deleteItem: deleteProject,
    });

    const [createModalOpen, setCreateModalOpen] = useState(false);
    const [editingProject, setEditingProject] =
        useState<ProjectListResponse | null>(null);

    const handleDelete = async (project: ProjectListResponse) => {
        try {
            await remove(project.id);
        } catch (error) {
            handleApiError(error);
        }
    };

    return (
        <>
            <ListPage
                title="Projects"
                description="Manage your projects and their boards."
                items={projects}
                searchPlaceholder="Search projects..."
                actionLabel="New Project"
                isLoading={isLoading}
                onAction={() => setCreateModalOpen(true)}
                getItemKey={(project) => project.id}
                searchFilter={(project, query) => {
                    const normalizedQuery = query.toLowerCase();

                    return (
                        project.name
                            .toLowerCase()
                            .includes(normalizedQuery) ||
                        project.description
                            .toLowerCase()
                            .includes(normalizedQuery)
                    );
                }}
                emptyTitle="No projects yet"
                emptyDescription="Create a project to organize your work."
                renderItem={(project) => (
                    <ProjectListItem
                        project={project}
                        onEdit={() => setEditingProject(project)}
                        onDelete={() => handleDelete(project)}
                    />
                )}
            />

            <CreateProjectModal
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onCreated={refresh}
            />

            {editingProject && (
                <UpdateProjectModal
                    open
                    project={editingProject}
                    onClose={() => setEditingProject(null)}
                    onUpdated={refresh}
                />
            )}
        </>
    );
}