import type {User} from "../authStore.ts";
import {ApiError, type ApiErrorResponse} from "../../api/ApiError.ts";

const baseUrl = "/api/auth";

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

    await assertResponseOk(response);

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

    await assertResponseOk(response);

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

    await assertResponseOk(response);

    return response.json();
}

const assertResponseOk = async (response: Response) => {
    if (!response.ok) {
        const errorResponse: ApiErrorResponse = await response.json();

        throw new ApiError(
            response.status,
            errorResponse
        );
    }
}
