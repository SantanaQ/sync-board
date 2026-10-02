import {Plus, Search} from "lucide-react";
import {type ReactNode, useMemo, useState} from "react";
import {LoadingSpinner} from "../../components/ui/LoadingSpinner.tsx";

export type ListPageProps<T> = {
    title: string;
    description?: string;

    items: T[];

    searchPlaceholder?: string;
    searchFilter?: (item: T, query: string) => boolean;

    renderItem: (item: T) => ReactNode;
    getItemKey: (item: T) => string;

    loadingText?: string;
    isLoading: boolean;

    actionLabel?: string;
    onAction?: () => void;

    emptyTitle?: string;
    emptyDescription?: string;

    toolbar?: ReactNode;
};

export function ListPage<T>({
    title,
    description,
    items,
    searchPlaceholder = "Search...",
    searchFilter,
    renderItem,
    getItemKey,
    loadingText,
    isLoading,
    actionLabel,
    onAction,
    emptyTitle = "Nothing present yet",
    emptyDescription = "Create a new entry",
    toolbar,
                            }: ListPageProps<T>) {
    const [query, setQuery] = useState("");

    const filteredItems = useMemo(() => {
        if (!query.trim() || !searchFilter) {
            return items;
        }

        return items.filter((item) => searchFilter(item, query));
    }, [items, query, searchFilter]);

    return (
        <main className="min-h-screen bg-app">
            <div className="mx-auto max-w-7xl px-6 py-8 lg:px-8">
                {/* Page Header */}
                <div className="mb-8 flex flex-col gap-5 lg:flex-row lg:items-end lg:justify-between">
                    <div>
                        <h1 className="text-2xl font-semibold tracking-tight text-primary">
                            {title}
                        </h1>

                        {description && (
                            <p className="mt-1 text-sm text-muted">{description}</p>
                        )}
                    </div>

                    {actionLabel && onAction && (
                        <button onClick={onAction} className="btn-primary">
                            <Plus className="h-4 w-4"/>
                            {actionLabel}
                        </button>
                    )}
                </div>

                {/* Toolbar */}
                <div className="mb-5 flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
                    <div className="relative max-w-sm flex-1">
                        <Search
                            className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted"/>

                        <input
                            value={query}
                            onChange={(event) => setQuery(event.target.value)}
                            placeholder={searchPlaceholder}
                            className="input pl-9"
                        />
                    </div>

                    {toolbar && <div>{toolbar}</div>}
                </div>

                {/* List */}
                <div className="card overflow-hidden">
                    {isLoading ? (
                        <LoadingSpinner text={loadingText} />
                    )
                    :
                    filteredItems && filteredItems.length > 0 ? (
                        <div className="divide-y divide-border">
                            {filteredItems.map((item) => (
                                <div key={getItemKey(item)}>{renderItem(item)}</div>
                            ))}
                        </div>
                    ) : (
                        <div className="px-6 py-16 text-center">
                            <div
                                className="mx-auto flex h-11 w-11 items-center justify-center rounded-xl bg-surface-muted">
                                <Search className="h-5 w-5 text-muted"/>
                            </div>

                            <h3 className="mt-4 text-sm font-medium text-primary">
                                {query ? "No results" : emptyTitle}
                            </h3>

                            <p className="mx-auto mt-1 max-w-sm text-sm text-muted">
                                {query
                                    ? `No results found for „${query}“.`
                                    : emptyDescription}
                            </p>
                        </div>
                    )}
                </div>
            </div>
        </main>
    );
}