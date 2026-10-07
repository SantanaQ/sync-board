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

const getCsrfToken = (): string | undefined => {
    const cookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='))

    return cookie?.split('=').slice(1).join('=')
}

const setHeaders = () => {
    const headers = new Headers()

    headers.set('Content-Type', 'application/json')
    headers.set('Accept', 'application/json')

    const csrfToken = getCsrfToken()

    if (csrfToken) {
        headers.set('X-XSRF-TOKEN', decodeURIComponent(csrfToken))
    }

    return headers
}

export const login = async (
    reqeust: LoginRequest
): Promise<void> => {
    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        headers: setHeaders(),
        credentials: "include",
        body: JSON.stringify(reqeust)
    });

    await assertResponseOk(response);
};

export const register = async (
    request: RegistrationRequest
): Promise<void> => {
    const response = await fetch(`${baseUrl}/register`, {
        method: "POST",
        headers: setHeaders(),
        credentials: "include",
        body: JSON.stringify(request)
    })

    await assertResponseOk(response);
}

export const logout = async (
): Promise<void> => {
    const response = await fetch(`${baseUrl}/logout`, {
        method: "POST",
        headers: setHeaders(),
        credentials: "include"
    })

    await assertResponseOk(response);
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
