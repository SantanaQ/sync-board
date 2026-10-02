import {
    ArrowRight,
    CheckCircle2,
    Clock3,
    FolderKanban,
    TrendingUp,
} from "lucide-react";
import {useAuth} from "../hooks/useAuth.ts";
import {today} from "../utils/formatDate.ts";
import {useNavigate} from "react-router-dom";

type Project = {
    id: string;
    name: string;
    description: string;
    boards: number;
    tasks: number;
    completedTasks: number;
    updatedAt: string;
    color?: string;
};

const projects: Project[] = [
    {
        id: "1",
        name: "Website Relaunch",
        description: "Redesign und technische Überarbeitung der Website",
        boards: 4,
        tasks: 28,
        completedTasks: 19,
        updatedAt: "vor 2 Stunden",
        color: "bg-indigo-500",
    },
    {
        id: "2",
        name: "Mobile App",
        description: "Neue Mobile App für iOS und Android",
        boards: 3,
        tasks: 17,
        completedTasks: 9,
        updatedAt: "gestern",
        color: "bg-emerald-500",
    },
    {
        id: "3",
        name: "Marketing",
        description: "Kampagnen, Content und Social Media",
        boards: 5,
        tasks: 42,
        completedTasks: 31,
        updatedAt: "vor 3 Tagen",
        color: "bg-amber-500",
    },
];

const activities = [
    {
        id: 1,
        text: "Max hat „Landingpage fertigstellen“ abgeschlossen",
        time: "vor 12 Min.",
    },
    {
        id: 2,
        text: "Anna hat ein neues Board in „Mobile App“ erstellt",
        time: "vor 1 Std.",
    },
    {
        id: 3,
        text: "Du wurdest zu „Marketing Website“ hinzugefügt",
        time: "vor 3 Std.",
    },
    {
        id: 4,
        text: "Projekt „Website Relaunch“ wurde aktualisiert",
        time: "gestern",
    },
];

export default function Dashboard() {

    const {user} = useAuth();
    const navigate = useNavigate();

    const totalTasks = projects.reduce((sum, project) => sum + project.tasks, 0);
    const completedTasks = projects.reduce(
        (sum, project) => sum + project.completedTasks,
        0
    );

    const completionRate = Math.round((completedTasks / totalTasks) * 100);

    return (
        <div className="min-h-screen bg-app">
            <main className="mx-auto max-w-7xl px-6 py-8 lg:px-8">
                {/* Header */}
                <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                    <div>
                        <p className="mb-1 text-sm font-medium text-muted">
                            {today()}
                        </p>

                        <h1 className="text-2xl font-semibold tracking-tight text-primary">
                            Welcome back, {user?.displayName}.
                        </h1>

                        <p className="mt-1 text-sm text-muted">
                            Here is your current workspace overview.
                        </p>
                    </div>
                </div>

                {/* Stats */}
                <section className="mb-8 grid gap-4 sm:grid-cols-2 xl:grid-cols-4">
                    <StatCard
                        icon={<FolderKanban className="h-5 w-5"/>}
                        label="Projects"
                        value={projects.length}
                    />

                    <StatCard
                        icon={<CheckCircle2 className="h-5 w-5"/>}
                        label="Completed Tasks"
                        value={`${completedTasks}/${totalTasks}`}
                        description={`${completionRate}% completed`}
                    />

                    <StatCard
                        icon={<Clock3 className="h-5 w-5"/>}
                        label="Open Tasks"
                        value={totalTasks - completedTasks}
                        description="across all projects"
                    />

                    <StatCard
                        icon={<TrendingUp className="h-5 w-5"/>}
                        label="Progress"
                        value={`${completionRate}%`}
                        description="current status"
                    />
                </section>

                <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
                    {/* Projects */}
                    <section className="card">
                        <div className="card-header">
                            <div>
                                <h2 className="section-title">My projects</h2>
                                <p className="section-description">
                                    Recently edited projects
                                </p>
                            </div>

                            <button className="btn-ghost cursor-pointer" onClick={() => navigate("/projects")}>
                                Show all
                                <ArrowRight className="h-4 w-4"/>
                            </button>
                        </div>

                        <div className="divide-y divide-border">
                            {projects.map((project) => {
                                const percentage = Math.round(
                                    (project.completedTasks / project.tasks) * 100
                                );

                                return (
                                    <div
                                        key={project.id}
                                        className="group flex items-center gap-4 py-4 p-4"
                                    >
                                        <div
                                            className={`h-10 w-10 shrink-0 rounded-xl ${
                                                project.color ?? "bg-accent"
                                            }`}
                                        />

                                        <div className="min-w-0 flex-1">
                                            <div className="flex items-center justify-between gap-4">
                                                <div className="min-w-0">
                                                    <h3 className="truncate text-sm font-medium text-primary">
                                                        {project.name}
                                                    </h3>

                                                    <p className="mt-0.5 truncate text-xs text-muted">
                                                        {project.description}
                                                    </p>
                                                </div>

                                                <span className="text-xs font-medium text-muted">
                                                  {percentage}%
                                                </span>
                                            </div>

                                            <div className="mt-3 h-1.5 overflow-hidden rounded-full bg-surface-muted">
                                                <div
                                                    className="h-full rounded-full bg-accent transition-all"
                                                    style={{width: `${percentage}%`}}
                                                />
                                            </div>

                                            <div className="mt-2 flex gap-4 text-xs text-muted">
                                                <span>{project.boards} Boards</span>
                                                <span>
                                                  {project.completedTasks}/{project.tasks} Tasks
                                                </span>
                                                <span>{project.updatedAt}</span>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                        </div>
                    </section>

                    {/* Activity */}
                    <section className="card">
                        <div className="card-header">
                            <div>
                                <h2 className="section-title">Activity</h2>
                                <p className="section-description">What happened recently</p>
                            </div>
                        </div>

                        <div className="space-y-5">
                            {activities.map((activity) => (
                                <div key={activity.id} className="flex gap-3">
                                    <div className="mt-1 h-2 w-2 shrink-0 rounded-full bg-accent"/>

                                    <div>
                                        <p className="text-sm leading-5 text-secondary">
                                            {activity.text}
                                        </p>

                                        <p className="mt-1 text-xs text-muted">
                                            {activity.time}
                                        </p>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </section>
                </div>
            </main>
        </div>
    );
}

function StatCard({
                      icon,
                      label,
                      value,
                      description,
                  }: {
    icon: React.ReactNode;
    label: string;
    value: string | number;
    description?: string;
}) {
    return (
        <div className="card p-5">
            <div className="mb-4 flex items-center justify-between">
        <span className="flex h-9 w-9 items-center justify-center rounded-lg bg-accent-soft text-accent">
          {icon}
        </span>
            </div>

            <p className="text-sm text-muted">{label}</p>

            <div className="mt-1 flex items-baseline gap-2">
        <span className="text-2xl font-semibold tracking-tight text-primary">
          {value}
        </span>

                {description && (
                    <span className="text-xs text-muted">{description}</span>
                )}
            </div>
        </div>
    );
}