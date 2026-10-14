import { assertResponseOk, headers } from "../utils/apiUtils.ts";
import { handleUnauthorized } from "../auth/authErrorHandler.ts";

const baseUrl = "/api";

export interface SpringPage<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

interface GetOptions {
    signal?: AbortSignal;
    params?: Record<string, string | number | boolean | undefined>;
}

const checkResponse = async (response: Response): Promise<void> => {
    if (response.status === 401) {
        handleUnauthorized();
    }

    await assertResponseOk(response);
};

const buildQueryString = (
    params: GetOptions["params"]
): string => {
    const searchParams = new URLSearchParams();

    Object.entries(params ?? {}).forEach(([key, value]) => {
        if (value !== undefined) {
            searchParams.set(key, String(value));
        }
    });

    const queryString = searchParams.toString();

    return queryString ? `?${queryString}` : "";
};

const getRequest = async <T>(
    url: string,
    options: GetOptions = {}
): Promise<T> => {
    const response = await fetch(
        `${baseUrl}${url}${buildQueryString(options.params)}`,
        {
            method: "GET",
            signal: options.signal,
            headers: headers(),
            credentials: "include",
        }
    );

    await checkResponse(response);

    return response.json();
};

export const get = async <T>(
    url: string,
    options?: GetOptions
): Promise<T> => {
    return getRequest<T>(url, options);
};

export const search = async <T>(
    url: string,
    query: string,
    options: {
        signal?: AbortSignal;
        searchParam?: string;
        page?: number;
        size?: number;
        params?: Record<string, string | number | boolean | undefined>;
    } = {}
): Promise<T[]> => {
    const normalizedQuery = query.trim();

    if (!normalizedQuery) {
        return [];
    }

    const {
        signal,
        searchParam = "search",
        page = 0,
        size = 10,
        params,
    } = options;

    const result = await getRequest<SpringPage<T>>(url, {
        signal,
        params: {
            ...params,
            [searchParam]: normalizedQuery,
            page,
            size,
        },
    });

    return result.content;
};

export const post = async <T, B = unknown>(
    url: string,
    body?: B
): Promise<T> => {
    const response = await fetch(`${baseUrl}${url}`, {
        method: "POST",
        headers: headers(),
        credentials: "include",
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    await checkResponse(response);

    return response.json();
};

export const del = async (url: string): Promise<void> => {
    const response = await fetch(`${baseUrl}${url}`, {
        method: "DELETE",
        headers: headers(),
        credentials: "include",
    });

    await checkResponse(response);
};

export const put = async <T, B = unknown>(
    url: string,
    body?: B
): Promise<T> => {
    const response = await fetch(`${baseUrl}${url}`, {
        method: "PUT",
        headers: headers(),
        credentials: "include",
        body: body !== undefined ? JSON.stringify(body) : undefined,
    });

    await checkResponse(response);

    return response.json();
};
