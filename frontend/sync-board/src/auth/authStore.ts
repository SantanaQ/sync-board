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
    user: User | null;
    authStatus: AuthStatus;
};








