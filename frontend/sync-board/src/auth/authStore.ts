import type {UserResponse} from "../api/types.ts";

export type AuthStatus =
    | "loading"
    | "authenticated"
    | "unauthenticated";

export type User = {
    id: string;
    displayName: string;
    email: string;
};

export type AuthState = {
    user: UserResponse | null;
    authStatus: AuthStatus;
};








