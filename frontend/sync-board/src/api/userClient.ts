import {get, search} from "./apiClient.ts";
import type {UserResponse} from "./types.ts";

export const me = async () =>
    get<UserResponse>("/users/me");

export const searchUsers = (
    query: string,
    signal?: AbortSignal
): Promise<UserResponse[]> => {
    return search<UserResponse>("/users", query, {
        signal,
        searchParam: "search",
        page: 0,
        size: 10,
    });
};