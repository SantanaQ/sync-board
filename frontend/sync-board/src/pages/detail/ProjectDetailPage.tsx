import { ArrowRight, CalendarDays, FolderKanban } from "lucide-react";
import { Link, useParams } from "react-router-dom";
import DetailPage from "./DetailPage";

export default function ProjectDetailPage() {
    const { projectId } = useParams();

    const project = {
        id: projectId ?? "1",
        name: "Website Redesign",
        description:
            "Neugestaltung der Unternehmenswebsite inklusive UX, Design und technischer Umsetzung.",
        createdAt: "12. September 2026",
    };

    return (
        <DetailPage
            title={project.name}
            description={project.description}
            backHref="/projects"
            backLabel="Projekte"
            actions={
                <button className="btn-primary">
                    Edit project
                </button>
            }
            meta={
                <div className="flex flex-wrap items-center gap-x-5 gap-y-2 text-sm text-muted">
                    <div className="flex items-center gap-2">
                        <CalendarDays className="h-4 w-4" />
                        Created on {project.createdAt}
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
                                    Information about this project
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
                                    The boards for this project
                                </p>
                            </div>
                        </div>

                        <div className="divide-border divide-y">
                            <Link
                                to={`/projects/${project.id}/boards/1`}
                                className="
                                    flex items-center justify-between
                                    px-5 py-4
                                    transition-colors
                                    hover:bg-surface-muted
                                "
                            >
                                <div className="flex items-center gap-3">
                                    <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent-soft">
                                        <FolderKanban className="h-4 w-4 text-accent" />
                                    </div>

                                    <div>
                                        <p className="text-sm font-medium text-primary">
                                            Website Board
                                        </p>

                                        <p className="mt-0.5 text-xs text-muted">
                                            4 Columns · 12 Cards
                                        </p>
                                    </div>
                                </div>

                                <ArrowRight className="h-4 w-4 text-muted" />
                            </Link>
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
                                    Projekt-ID
                                </p>

                                <p className="mt-1 font-mono text-xs text-secondary">
                                    {project.id}
                                </p>
                            </div>
                        </div>
                    </section>
                </aside>
            </div>
        </DetailPage>
    );
}