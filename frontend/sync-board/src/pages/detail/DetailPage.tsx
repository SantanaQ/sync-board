import type { ReactNode } from "react";
import { ArrowLeft } from "lucide-react";
import { Link } from "react-router-dom";

type DetailPageProps = {
    title: string;
    description?: string;

    backHref?: string;
    backLabel?: string;

    actions?: ReactNode;

    meta?: ReactNode;

    children: ReactNode;
};

export default function DetailPage({
                                       title,
                                       description,
                                       backHref,
                                       backLabel = "back",
                                       actions,
                                       meta,
                                       children,
                                   }: DetailPageProps) {
    return (
        <div className="min-h-screen bg-app">
            <div className="mx-auto w-full max-w-7xl px-4 py-6 sm:px-6 lg:px-8 lg:py-8">

                {/* Back */}
                {backHref && (
                    <Link
                        to={backHref}
                        className="
                            mb-6 inline-flex items-center gap-2
                            text-sm font-medium
                            text-muted
                            transition-colors
                            hover:text-primary
                        "
                    >
                        <ArrowLeft className="h-4 w-4" />
                        {backLabel}
                    </Link>
                )}

                {/* Header */}
                <div className="mb-8">
                    <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                        <div className="min-w-0">
                            <h1 className="text-2xl font-semibold tracking-tight text-primary">
                                {title}
                            </h1>

                            {description && (
                                <p className="mt-1 max-w-2xl text-sm leading-6 text-muted">
                                    {description}
                                </p>
                            )}
                        </div>

                        {actions && (
                            <div className="flex shrink-0 items-center gap-2">
                                {actions}
                            </div>
                        )}
                    </div>

                    {meta && (
                        <div className="mt-5">
                            {meta}
                        </div>
                    )}
                </div>

                {/* Content */}
                <div>
                    {children}
                </div>
            </div>
        </div>
    );
}