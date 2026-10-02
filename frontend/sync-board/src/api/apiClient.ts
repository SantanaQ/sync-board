import {assertResponseOk, headers} from "../utils/apiUtils.ts";

const baseUrl = "/api";

export const get = async <T>(url: string): Promise<T> => {
    const response = await fetch(baseUrl + url, {
        method: "GET",
        headers: headers(),
        credentials: "include",
    });

    await assertResponseOk(response);

    return response.json();
};

export const post = async <T, B = unknown>(
    url: string,
    body?: B
): Promise<T> => {
    const response = await fetch(baseUrl + url, {
        method: "POST",
        headers: headers(),
        credentials: "include",
        body: body ? JSON.stringify(body) : undefined,
    });

    await assertResponseOk(response);

    return response.json();
};

export const del = async (url: string): Promise<void> => {
    const response = await fetch(baseUrl + url, {
        method: "DELETE",
        headers: headers(),
        credentials: "include",
    })

    await assertResponseOk(response);
}

export const put = async <T, B = unknown>(
    url: string,
    body?: B
): Promise<T> => {
    const response = await fetch(baseUrl + url, {
        method: "PUT",
        headers: headers(),
        credentials: "include",
        body: body ? JSON.stringify(body) : undefined,
    })

    await assertResponseOk(response);

    return response.json();
}



