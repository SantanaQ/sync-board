import {useState} from "react";
import {ListPage} from "./ListPage";
import {getProjects, deleteProject} from "../../api/projectClient.ts";
import type {ProjectListResponse} from "../../api/types.ts";
import {useCrudList} from "../../hooks/useCrudList.ts";
import CreateProjectModal from "../../components/project/CreateProjectModal.tsx";
import {ProjectListItem} from "./ProjectListItem.tsx";
import {useToast} from "../../hooks/useToast.ts";

export default function ProjectListPage() {
    const {showSuccess} = useToast();

    const {
        items: projects,
        isLoading,
        refresh,
    } = useCrudList<ProjectListResponse>({
        fetchItems: getProjects,
        deleteItem: deleteProject,
    });

    const [createModalOpen, setCreateModalOpen] = useState(false);

    const onCreated = () => {
        refresh().then(() => showSuccess("Successfully created project."));
    }

    const onUpdated = () => {
        refresh().then(() => showSuccess("Successfully updated project."));
    }

    const onDeleted = () => {
        refresh().then(() => showSuccess("Successfully deleted project."));
    }


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
                        onEdited={onUpdated}
                        onDeleted={onDeleted}
                    />
                )}
            />
            <CreateProjectModal
                open={createModalOpen}
                onClose={() => setCreateModalOpen(false)}
                onCreated={onCreated}
            />
        </>
    );
}