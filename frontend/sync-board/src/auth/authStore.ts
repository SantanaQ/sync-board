import type {LoginRequest, RegistrationRequest} from "./api/authApi.ts";

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
    accessToken: string | null;
    authStatus: AuthStatus;
};

export type AuthContextValue = {
    user: User | null;
    authStatus: AuthStatus;

    login: (request : LoginRequest) => Promise<void>;
    register: (request: RegistrationRequest) => Promise<void>;
    logout: () => Promise<void>;
};






