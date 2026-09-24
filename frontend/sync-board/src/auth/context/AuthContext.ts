import { createContext } from "react";
import type {AuthStatus, User} from "../authStore.ts";
import type {LoginRequest, RegistrationRequest} from "../api/authClient.ts";

type AuthContextValue = {
    user: User | null;
    authStatus: AuthStatus;

    login: (request : LoginRequest) => Promise<void>;
    register: (request: RegistrationRequest) => Promise<void>;
    logout: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextValue | undefined>(
    undefined
);