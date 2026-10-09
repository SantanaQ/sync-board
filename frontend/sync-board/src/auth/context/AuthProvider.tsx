import {type ReactNode, useEffect, useState} from "react";
import type { AuthState } from "../authStore.ts";
import * as authClient from "../api/authClient.ts";
import * as userClient from "../../api/userClient.ts"
import type {LoginRequest, RegistrationRequest} from "../api/authClient.ts";
import { AuthContext } from "./AuthContext.ts";
import {registerUnauthorizedHandler} from "../authErrorHandler.ts";


export function AuthProvider({ children }: {children: ReactNode}) {
    const [authState, setAuthState] = useState<AuthState>({
        user: null,
        authStatus: 'loading',
    });

    async function login(credentials : LoginRequest) {
         await authClient.login(credentials);


        const user = await userClient.me();

        setAuthState({
            user,
            authStatus: "authenticated"
        });
    }

    async function register(data : RegistrationRequest) {
        await authClient.register(data);

        const user = await userClient.me();

        setAuthState({
            user,
            authStatus: "authenticated"
        });
    }

    async function logout() {
        await authClient.logout();

        setAuthState({
            user: null,
            authStatus: "unauthenticated"
        })
    }

    useEffect(() => {
        async function initializeAuth() {
            try {
                const user = await userClient.me();

                setAuthState({
                    user,
                    authStatus: "authenticated",
                });
            } catch {
                setAuthState({
                    user: null,
                    authStatus: "unauthenticated",
                });
            }
        }

        initializeAuth();
    }, []);


    useEffect(() => {
        return registerUnauthorizedHandler(() => {
            setAuthState({
                user: null,
                authStatus: "unauthenticated",
            });
        });
    }, []);


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

