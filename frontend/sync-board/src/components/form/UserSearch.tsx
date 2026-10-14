import {useCallback} from "react";

import { SearchInput } from "./SearchInput";
import ProfileIcon from "../../assets/ProfileIcon";
import { searchUsers } from "../../api/userClient.ts";
import type { UserResponse } from "../../api/types.ts";

interface UserSearchProps {
    id?: string;
    label?: string;
    error?: string;
    maxSelectedItems?: number;
    onUserSelect: (user: UserResponse) => void;
    onSelectionChange?: (users: UserResponse[]) => void;
}

export function UserSearch({
    id="user",
    label="Search users",
    error,
    maxSelectedItems = Infinity,
    onUserSelect,
    onSelectionChange,
}: UserSearchProps) {
    const handleUserSearch = useCallback(
        (query: string, signal: AbortSignal) =>
            searchUsers(query, signal),
        []
    );

    return (
        <SearchInput<UserResponse>
            id={id}
            label={label}
            error={error}
            maxSelectedItems={maxSelectedItems}
            placeholder="Type a name or email..."
            onSearch={handleUserSearch}
            onSelect={onUserSelect}
            onSelectionChange={onSelectionChange}
            getItemKey={(user) => user.id}
            debounceMs={300}
            minSearchLength={2}
            emptyMessage="No users found"
            loadingMessage="Searching users..."
            searchErrorMessage="Could not load users. Please try again."
            renderItem={(user, active) => (
                <div
                    className={`
                        flex items-center gap-3
                        ${active ? "text-primary" : ""}
                        `}
                >
                    <div
                        className="
                            flex h-8 w-8 shrink-0 items-center
                            justify-center rounded-full
                            bg-surface-muted text-muted
                        "
                    >
                        <ProfileIcon className="h-4 w-4" />
                    </div>

                    <div className="min-w-0 flex-1">
                        <p className="truncate text-xs font-semibold text-primary">
                            {user.displayName}
                        </p>

                        <p className="truncate text-[11px] text-muted">
                            {user.email}
                        </p>
                    </div>
                </div>
            )}
            renderSelectedItem={(user) => (
                <span className="truncate">
                    {user.displayName}
                </span>
            )}
        />
    );
}