import {assertResponseOk, headers} from "../../utils/apiUtils.ts";

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


export const login = async (
    reqeust: LoginRequest
): Promise<void> => {
    const response = await fetch(`${baseUrl}/login`, {
        method: "POST",
        headers: headers(),
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
        headers: headers(),
        credentials: "include",
        body: JSON.stringify(request)
    })

    await assertResponseOk(response);
}

export const logout = async (
): Promise<void> => {
    const response = await fetch(`${baseUrl}/logout`, {
        method: "POST",
        headers: headers(),
        credentials: "include"
    })

    await assertResponseOk(response);
}


