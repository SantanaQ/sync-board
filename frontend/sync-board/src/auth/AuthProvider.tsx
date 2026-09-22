import {type ReactNode, useState} from "react";
import type {AuthState} from "./authStore.ts";
import * as authApi from "./api/authApi";
import type {LoginRequest, RegistrationRequest} from "./api/authApi";
import { AuthContext } from "./AuthContext.ts";

export function AuthProvider({ children }: {children: ReactNode}) {
    const [authState, setAuthState] = useState<AuthState>({
        user: null,
        accessToken: null,
        authStatus: 'loading',
    });

    async function login(credentials : LoginRequest) {
        const { accessToken } = await authApi.login(credentials);

        const user = await authApi.me(accessToken);

        setAuthState({
            user,
            accessToken,
            authStatus: "authenticated"
        });
    }

    async function register(data : RegistrationRequest) {
        const { accessToken } = await authApi.register(data);

        const user = await authApi.me(accessToken);

        setAuthState({
            user,
            accessToken,
            authStatus: "authenticated"
        });
    }

    async function logout() {
        setAuthState({
            user: null,
            accessToken: null,
            authStatus: "unauthenticated"
        })
    }

    return (
        <AuthContext.Provider
            value={{
                user: authState.user,
                authStatus: authState.authStatus,
                login: login,
                register: register,
                logout: logout
            }}
        >
            {children}
        </AuthContext.Provider>
    )

}

