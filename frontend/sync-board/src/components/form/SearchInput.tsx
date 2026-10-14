import {
    useState,
    useEffect,
    useRef,
    useId,
    useCallback,
    type ComponentPropsWithoutRef,
    type KeyboardEvent,
    type ReactNode,
} from "react";

import { FormField } from "./FormField";
import { inputStyles } from "./inputStyles";

interface SearchInputProps<T>
    extends Omit<
        ComponentPropsWithoutRef<"input">,
        | "onChange"
        | "value"
        | "onSelect"
        | "onFocus"
        | "onKeyDown"
        | "type"
    > {
    id: string;
    label: string;
    error?: string;

    onSearch: (
        query: string,
        signal: AbortSignal
    ) => Promise<T[]>;

    renderItem: (item: T, active: boolean) => ReactNode;
    getItemKey: (item: T) => string;
    onSelect?: (item: T) => void;
    maxSelectedItems?: number;

    // Optional controlled selection
    selectedItems?: T[];
    onSelectionChange?: (items: T[]) => void;

    // Optional rendering for selected chips
    renderSelectedItem?: (item: T) => ReactNode;

    debounceMs?: number;
    minSearchLength?: number;
    emptyMessage?: string;
    loadingMessage?: string;
    searchErrorMessage?: string;
}

export function SearchInput<T>({
    id,
    label,
    error,
    required,
    className = "",
    onSearch,
    renderItem,
    getItemKey,
    maxSelectedItems = Infinity,
    onSelect,
    selectedItems: controlledSelectedItems,
    onSelectionChange,
    renderSelectedItem,
    debounceMs = 300,
    minSearchLength = 1,
    emptyMessage = "No results found",
    loadingMessage = "Searching...",
    searchErrorMessage = "Failed to fetch search results.",
    placeholder = "Search...",
    disabled,
    ...props
}: SearchInputProps<T>) {
    const [query, setQuery] = useState("");
    const [results, setResults] = useState<T[]>([]);
    const [internalSelectedItems, setInternalSelectedItems] =
        useState<T[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isOpen, setIsOpen] = useState(false);
    const [searchError, setSearchError] = useState<string | null>(null);
    const [activeIndex, setActiveIndex] = useState(-1);

    const wrapperRef = useRef<HTMLDivElement>(null);
    const inputRef = useRef<HTMLInputElement>(null);
    const requestVersionRef = useRef(0);

    const generatedId = useId();
    const listboxId = `${id}-listbox`;
    const statusId = `${id}-status`;

    const selectedItems =
        controlledSelectedItems ?? internalSelectedItems;

    const normalizedQuery = query.trim();
    const canSearch = normalizedQuery.length >= minSearchLength;

    // Never show already-selected records in the dropdown.
    const selectedKeys = new Set(
        selectedItems.map((item) => getItemKey(item))
    );

    const availableResults = results.filter(
        (item) => !selectedKeys.has(getItemKey(item))
    );

    const updateSelectedItems = useCallback(
        (nextItems: T[]) => {
            if (controlledSelectedItems === undefined) {
                setInternalSelectedItems(nextItems);
            }

            onSelectionChange?.(nextItems);
        },
        [controlledSelectedItems, onSelectionChange]
    );

    const closeDropdown = useCallback(() => {
        setIsOpen(false);
        setActiveIndex(-1);
    }, []);


    const handleSelect = useCallback(
        (item: T) => {
            const key = getItemKey(item);

            // Prevent duplicate selection.
            if (
                selectedItems.some(
                    (selected) => getItemKey(selected) === key
                )
            ) {
                closeDropdown();
                return;
            }

            const nextItems =
                selectedItems.length >= maxSelectedItems
                    ? [...selectedItems.slice(1), item]
                    : [...selectedItems, item];

            updateSelectedItems(nextItems);
            onSelect?.(item);

            setQuery("");
            setResults([]);
            setSearchError(null);
            closeDropdown();
            inputRef.current?.focus();
        },
        [
            selectedItems,
            getItemKey,
            maxSelectedItems,
            updateSelectedItems,
            onSelect,
            closeDropdown,
        ]
    );


    const handleRemove = useCallback(
        (item: T) => {
            const key = getItemKey(item);

            updateSelectedItems(
                selectedItems.filter(
                    (selected) => getItemKey(selected) !== key
                )
            );

            inputRef.current?.focus();
        },
        [selectedItems, getItemKey, updateSelectedItems]
    );

    // Search with debounce and stale-request protection.
    useEffect(() => {
        const requestVersion = ++requestVersionRef.current;

        if (!canSearch || disabled) {
            setResults([]);
            setIsOpen(false);
            setIsLoading(false);
            setSearchError(null);
            setActiveIndex(-1);
            return;
        }

        const controller = new AbortController();

        setIsLoading(false);
        setSearchError(null);
        setActiveIndex(-1);

        const timeoutId = setTimeout(async () => {
            setIsLoading(true);

            try {
                const data = await onSearch(
                    normalizedQuery,
                    controller.signal
                );

                if (
                    controller.signal.aborted ||
                    requestVersion !== requestVersionRef.current
                ) {
                    return;
                }

                setResults(data);
                setIsOpen(true);
            } catch {
                if (
                    controller.signal.aborted ||
                    requestVersion !== requestVersionRef.current
                ) {
                    return;
                }

                setResults([]);
                setSearchError(searchErrorMessage);
                setIsOpen(true);
            } finally {
                if (
                    !controller.signal.aborted &&
                    requestVersion === requestVersionRef.current
                ) {
                    setIsLoading(false);
                }
            }
        }, debounceMs);

        return () => {
            clearTimeout(timeoutId);
            controller.abort();
        };
    }, [
        normalizedQuery,
        canSearch,
        disabled,
        onSearch,
        debounceMs,
        searchErrorMessage,
    ]);

    // Close the dropdown when clicking outside.
    useEffect(() => {
        function handlePointerDown(event: PointerEvent) {
            if (
                wrapperRef.current &&
                !wrapperRef.current.contains(event.target as Node)
            ) {
                closeDropdown();
            }
        }

        document.addEventListener("pointerdown", handlePointerDown);

        return () => {
            document.removeEventListener(
                "pointerdown",
                handlePointerDown
            );
        };
    }, [closeDropdown]);

    const handleKeyDown = (
        event: KeyboardEvent<HTMLInputElement>
    ) => {
        if (event.key === "Escape") {
            closeDropdown();
            return;
        }

        if (event.key === "ArrowDown") {
            if (availableResults.length === 0 || isLoading) return;

            event.preventDefault();
            setIsOpen(true);
            setActiveIndex((current) =>
                current < availableResults.length - 1
                    ? current + 1
                    : 0
            );
            return;
        }

        if (event.key === "ArrowUp") {
            if (availableResults.length === 0 || isLoading) return;

            event.preventDefault();
            setIsOpen(true);
            setActiveIndex((current) =>
                current > 0
                    ? current - 1
                    : availableResults.length - 1
            );
            return;
        }

        if (event.key === "Enter" && isOpen && activeIndex >= 0) {
            event.preventDefault();

            const item = availableResults[activeIndex];

            if (item !== undefined) {
                handleSelect(item);
            }
        }
    };

    const showDropdown = isOpen && canSearch && !disabled;

    return (
        <FormField
            id={id}
            label={label}
            error={error}
            required={required}
        >
            <div ref={wrapperRef} className="relative w-full">
                <div className="relative flex items-center">
                    <input
                        {...props}
                        ref={inputRef}
                        id={id}
                        type="search"
                        value={query}
                        disabled={disabled}
                        required={required}
                        placeholder={placeholder}
                        autoComplete="off"
                        aria-invalid={!!error}
                        aria-describedby={
                            error
                                ? `${id}-error`
                                : showDropdown
                                    ? statusId
                                    : undefined
                        }
                        role="combobox"
                        aria-autocomplete="list"
                        aria-expanded={showDropdown}
                        aria-controls={
                            showDropdown ? listboxId : undefined
                        }
                        aria-activedescendant={
                            showDropdown && activeIndex >= 0
                                ? `${generatedId}-option-${activeIndex}`
                                : undefined
                        }
                        onChange={(event) => {
                            setQuery(event.target.value);
                            setActiveIndex(-1);
                        }}
                        onFocus={() => {
                            if (canSearch && !disabled) {
                                setIsOpen(true);
                            }
                        }}
                        onKeyDown={handleKeyDown}
                        className={`
                            ${inputStyles}
                            ${
                                error
                                    ? "border-red-500 focus:border-red-500 focus:ring-red-500/20"
                                    : ""
                            }
                            pr-9 ${className}
                            `}
                    />

                    {isLoading && (
                        <div
                            className="absolute right-3 text-muted"
                            aria-hidden="true"
                        >
                            <svg
                                className="h-4 w-4 animate-spin"
                                viewBox="0 0 24 24"
                                fill="none"
                            >
                                <circle
                                    className="opacity-25"
                                    cx="12"
                                    cy="12"
                                    r="10"
                                    stroke="currentColor"
                                    strokeWidth="4"
                                />
                                <path
                                    className="opacity-75"
                                    fill="currentColor"
                                    d="M4 12a8 8 0 018-8v8H4z"
                                />
                            </svg>
                        </div>
                    )}
                </div>

                {/* Selected records */}
                {selectedItems.length > 0 && (
                    <div
                        className="mt-2 flex flex-wrap gap-2"
                        aria-label="Selected items"
                    >
                        {selectedItems.map((item) => (
                            <div
                                key={getItemKey(item)}
                                className="
                                    inline-flex max-w-full items-center gap-2
                                    rounded-full border border-border
                                    bg-surface-muted px-3 py-1.5
                                    text-xs text-primary
                                "
                            >
                                <div className="min-w-0 truncate">
                                    {renderSelectedItem
                                        ? renderSelectedItem(item)
                                        : renderItem(item, false)}
                                </div>

                                <button
                                    type="button"
                                    disabled={disabled}
                                    aria-label="Remove selected item"
                                    title="Remove"
                                    onClick={() => handleRemove(item)}
                                    className="
                                        flex h-5 w-5 shrink-0 items-center
                                        justify-center rounded-full
                                        text-muted transition-colors
                                        hover:bg-surface hover:text-primary
                                        disabled:cursor-not-allowed
                                        disabled:opacity-50
                                    "
                                >
                                    <svg
                                        viewBox="0 0 20 20"
                                        fill="none"
                                        className="h-3.5 w-3.5"
                                        aria-hidden="true"
                                    >
                                        <path
                                            d="M5 5L15 15M15 5L5 15"
                                            stroke="currentColor"
                                            strokeWidth="1.8"
                                            strokeLinecap="round"
                                        />
                                    </svg>
                                </button>
                            </div>
                        ))}
                    </div>
                )}

                <div
                    id={statusId}
                    className="sr-only"
                    role="status"
                    aria-live="polite"
                >
                    {isLoading ? loadingMessage : ""}
                </div>

                {showDropdown && (
                    <div
                        id={listboxId}
                        role="listbox"
                        aria-label={`${label} results`}
                        className="
                            card absolute left-0 top-full z-50 mt-1.5
                            max-h-60 w-full overflow-y-auto p-1
                            shadow-lg divide-y divide-border
                        "
                    >
                        {searchError ? (
                            <div
                                className="px-3 py-2.5 text-center text-xs text-red-500"
                                role="alert"
                            >
                                {searchError}
                            </div>
                        ) : isLoading ? (
                            <div className="px-3 py-2.5 text-center text-xs text-muted">
                                {loadingMessage}
                            </div>
                        ) : availableResults.length === 0 ? (
                            <div className="px-3 py-2.5 text-center text-xs text-muted">
                                {emptyMessage}
                            </div>
                        ) : (
                            availableResults.map((item, index) => {
                                const active = index === activeIndex;

                                return (
                                    <div
                                        key={getItemKey(item)}
                                        id={`${generatedId}-option-${index}`}
                                        role="option"
                                        aria-selected={active}
                                        onMouseDown={(event) => {
                                            event.preventDefault();
                                        }}
                                        onMouseEnter={() => {
                                            setActiveIndex(index);
                                        }}
                                        onClick={() => handleSelect(item)}
                                        className={`
                                            cursor-pointer rounded-md p-2
                                            transition-colors
                                            ${
                                                active
                                                    ? "bg-surface-muted"
                                                    : "hover:bg-surface-muted"
                                            }
                                            `}
                                    >
                                        {renderItem(item, active)}
                                    </div>
                                );
                            })
                        )}
                    </div>
                )}
            </div>
        </FormField>
    );
}