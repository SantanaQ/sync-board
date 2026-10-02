import {ApiError, type ApiErrorResponse} from "../api/ApiError.ts";

const getCsrfToken = (): string | undefined => {
    const cookie = document.cookie
        .split('; ')
        .find(row => row.startsWith('XSRF-TOKEN='))

    return cookie?.split('=').slice(1).join('=')
}

export const headers = () => {
    const headers = new Headers()

    headers.set('Content-Type', 'application/json')
    headers.set('Accept', 'application/json')

    const csrfToken = getCsrfToken()

    if (csrfToken) {
        headers.set('X-XSRF-TOKEN', decodeURIComponent(csrfToken))
    }

    return headers
}

export const assertResponseOk = async (response: Response) => {
    if (!response.ok) {
        const errorResponse: ApiErrorResponse = await response.json();

        throw new ApiError(
            response.status,
            errorResponse
        );
    }
}