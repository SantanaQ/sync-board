import {ArrowRight, CalendarDays, FolderKanban, Plus} from "lucide-react";
import { Link, useParams } from "react-router-dom";
import DetailPage from "./DetailPage";
import * as projectClient from "../../api/projectClient"
import * as boardClient from "../../api/boardClient"
import * as memberClient from "../../api/projectMemberClient";
import {useEffect, useState} from "react";
import type {BoardListResponse, ProjectMemberResponse, ProjectResponse} from "../../api/types.ts";
import UpdateProjectModal from "../../components/project/UpdateProjectModal.tsx";
import CreateBoardModal from "../../components/board/CreateBoardModal.tsx";
import {formatDateStr} from "../../utils/formatDate.ts";
import {useToast} from "../../hooks/useToast.ts";
import {MemberBadge} from "../../components/project/member/MemberBadge.tsx";
import AddMemberModal from "../../components/project/member/AddMemberModal.tsx";

export default function ProjectDetailPage() {
    const { projectId } = useParams();
    const { showSuccess } = useToast();

    const [project, setProject] = useState<ProjectResponse | undefined>();
    const [boards, setBoards] = useState<BoardListResponse[]>([]);
    const [projectMembers, setProjectMembers] = useState<ProjectMemberResponse[]>([]);

    const [updateModalOpen, setUpdateModalOpen] = useState<boolean>(false);
    const [createBoardModalOpen, setCreateBoardModalOpen] = useState<boolean>(false);
    const [addMemberModalOpen, setAddMemberModalOpen] = useState<boolean>(false);

    const refresh = async () => {
        if(!projectId) {
            return;
        }

        projectClient.getProject(projectId).then((response) => {
            setProject(response);
        })

        boardClient.getBoards(projectId).then((response) => {
            setBoards(response)
        })

        memberClient.getProjectMembers(projectId).then((response) => {
            setProjectMembers(response);
        })
    }

    useEffect(() => {
        refresh();
    }, []);

    const onUpdated = () => {
        refresh().then(() => showSuccess("Successfully updated project."));
    }

    const onCreated = () => {
        refresh().then(() => showSuccess("Successfully created board."));
    }

    const onAdded = () => {
        refresh().then(() => showSuccess("Successfully added project member."));
    }

    const onUpdatedMember = () => {
        refresh().then(() => showSuccess("Successfully updated project member."));
    }


    if (!project) {
        return null;
    }

    return (
        <>
            <DetailPage
                title={project.name}
                description=""
                backHref="/projects"
                backLabel="Projects"
                actions={
                    <button onClick={() => setUpdateModalOpen(true)} className="btn-primary">
                        Edit project
                    </button>
                }
                meta={
                    <div className="flex flex-wrap items-center gap-x-5 gap-y-2 text-sm text-muted">
                        <div className="flex items-center gap-2">
                            <CalendarDays className="h-4 w-4" />
                            Created on {formatDateStr(project.createdAt)}
                        </div>

                        <div className="flex items-center gap-2">
                            <FolderKanban className="h-4 w-4" />
                            Project
                        </div>
                    </div>
                }
            >
                <div className="grid gap-6 lg:grid-cols-3">

                    {/* Main */}
                    <div className="space-y-6 lg:col-span-2">

                        <section className="card">
                            <div className="card-header">
                                <div>
                                    <h2 className="section-title">
                                        Overview
                                    </h2>

                                    <p className="section-description">
                                        Description of this project
                                    </p>
                                </div>
                            </div>

                            <div className="px-5 pb-5">
                                <p className="text-sm leading-6 text-secondary">
                                    {project.description}
                                </p>
                            </div>
                        </section>

                        {/* Boards */}
                        <section className="card">
                            <div className="card-header">
                                <div>
                                    <h2 className="section-title">
                                        Boards
                                    </h2>

                                    <p className="section-description">
                                        Organize multiple units working on this project
                                    </p>
                                </div>
                                <button onClick={() => setCreateBoardModalOpen(true)} className="btn-primary">
                                    <Plus className="h-4 w-4"/>
                                    New board
                                </button>
                            </div>

                            <div className="divide-border divide-y">
                                {boards.length > 0 ? (
                                    boards.map((board) => (
                                        <div key={board.id}>
                                            <Link
                                                to={`/projects/${project.id}/boards/${board.id}`}
                                                className="
                                                        flex items-center justify-between
                                                        px-5 py-4
                                                        transition-colors
                                                        hover:bg-surface-muted
                                                    "
                                                state={{ parent: project }}
                                            >
                                                <div className="flex items-center gap-3">
                                                    <div
                                                        className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent-soft">
                                                        <FolderKanban className="h-4 w-4 text-accent"/>
                                                    </div>

                                                    <div>
                                                        <p className="text-sm font-medium text-primary">
                                                            {board.name}
                                                        </p>

                                                        <p className="mt-0.5 text-xs text-muted">
                                                            4 Columns · 12 Cards
                                                        </p>
                                                    </div>
                                                </div>

                                                <ArrowRight className="h-4 w-4 text-muted"/>
                                            </Link>
                                        </div>
                                    ))
                            ) : (
                                <div className="px-5 py-8 text-center text-sm text-muted">
                                    No boards yet...
                                </div>
                            )}
                            </div>
                        </section>
                    </div>

                    {/* Sidebar */}
                    <aside className="space-y-6">
                        <section className="card">
                            <div className="card-header">
                                <h2 className="section-title">
                                    Project information
                                </h2>
                            </div>

                            <div className="space-y-4 px-5 pb-5">
                                <div>
                                    <p className="text-xs text-muted">
                                        Name
                                    </p>

                                    <p className="mt-1 text-sm font-medium text-primary">
                                        {project.name}
                                    </p>
                                </div>

                                <div>
                                    <p className="text-xs text-muted">
                                        Project-ID
                                    </p>

                                    <p className="mt-1 font-mono text-xs text-secondary">
                                        {project.id}
                                    </p>
                                </div>
                                <div>
                                    <p className="text-xs text-muted">
                                        Ownership
                                    </p>

                                    <p className="mt-1 font-mono text-xs text-secondary">
                                        <MemberBadge member={
                                            {
                                                user: project.owner,
                                                role: "OWNER"
                                            }}
                                            enableModal={false}
                                        />
                                    </p>
                                </div>
                            </div>
                        </section>
                        <section className="card">
                            <div className="card-header">
                                <h2 className="section-title">
                                    Members
                                </h2>
                                <button onClick={() => setAddMemberModalOpen(true)} className="btn-primary">
                                <Plus className="h-4 w-4"/>
                                    Add member
                                </button>

                            </div>

                            <div className="space-y-4 px-5 pb-5">
                                {projectMembers && projectMembers.length > 0 ? (
                                    projectMembers.map((member) => (
                                        <MemberBadge
                                            className="m-1"
                                            key={member.user.email}
                                            member={member}
                                            enableModal={member.user.id !== project.owner.id}
                                            onUpdated={onUpdatedMember}
                                        />
                                    ))
                                ) : (
                                    <div className="px-5 py-8 text-center text-sm text-muted">
                                        no contributors yet...
                                    </div>
                                )}
                            </div>
                        </section>
                    </aside>
                </div>
            </DetailPage>
            <UpdateProjectModal
                open={updateModalOpen}
                project={project}
                onClose={() => setUpdateModalOpen(false)}
                onUpdated={onUpdated}
            />
            <CreateBoardModal
                open={createBoardModalOpen}
                onClose={() => setCreateBoardModalOpen(false)}
                onCreated={onCreated}
            />
            <AddMemberModal
                open={addMemberModalOpen}
                onClose={() => setAddMemberModalOpen(false)}
                onCreated={onAdded}
            />
        </>
    );
}