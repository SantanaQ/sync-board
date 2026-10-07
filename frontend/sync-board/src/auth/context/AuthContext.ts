import { createContext } from "react";
import type {AuthStatus} from "../authStore.ts";
import type {LoginRequest, RegistrationRequest} from "../api/authClient.ts";
import type {UserResponse} from "../../api/types.ts";

type AuthContextValue = {
    user: UserResponse | null;
    authStatus: AuthStatus;

    login: (request : LoginRequest) => Promise<void>;
    register: (request: RegistrationRequest) => Promise<void>;
    logout: () => Promise<void>;
};

export const AuthContext = createContext<AuthContextValue | undefined>(
    undefined
);