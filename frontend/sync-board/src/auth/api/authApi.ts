import type {User} from "../authStore.ts";

const baseUrl = "http://localhost:8080/api/auth";

export type LoginRequest = {
    email: string;
    password: string;
}

export type RegistrationRequest = {
    displayName: string;
    email: string;
    password: string;
    passwordConfirmation: string;
}

export type AuthResponse = {
    accessToken: string;
}

const setHeaders = () => {
    const headers: Headers = new Headers()
    headers.set('Content-Type', 'application/json')
    headers.set('Accept', 'application/json')
    return headers
}

export const login = async (
    reqeust: LoginRequest
): Promise<AuthResponse> => {
    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        headers: setHeaders(),
        body: JSON.stringify(reqeust)
    });

    return response.json();
};

export const register = async (
    request: RegistrationRequest
): Promise<AuthResponse> => {
    const response = await fetch(`${baseUrl}/register`, {
        method: "POST",
        headers: setHeaders(),
        body: JSON.stringify(request)
    })

    return response.json();
}

export const me = async (
    accessToken: string,
):  Promise<User> => {
    const response = await fetch(`${baseUrl}/me`, {
        method: "GET",
        headers: {
            ...setHeaders(),
            Authorization: `Bearer ${accessToken}`
        }
    })

    return response.json();
}
