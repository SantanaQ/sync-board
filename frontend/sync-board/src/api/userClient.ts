import {get} from "./apiClient.ts";
import type {UserResponse} from "./types.ts";

export const me = async () =>
    get<UserResponse>("/users/me");