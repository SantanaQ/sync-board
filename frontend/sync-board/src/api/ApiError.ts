import {type ErrorCodeType} from "./ErrorCode.ts";

export type ApiErrorResponse = {
    code: string;
    message: string;
    timestamp: string;
    errors?: Record<string, string>;
};

export class ApiError extends Error {
    readonly status: number;
    readonly code:  ErrorCodeType;
    readonly timestamp: string;
    readonly errors: Record<string, string>;

    constructor(
        status: number,
        response: ApiErrorResponse
    ) {
        super(response.message);

        this.name = "ApiError";
        this.status = status;
        this.code = response.code as ErrorCodeType;
        this.timestamp = response.timestamp;
        this.errors = response.errors ?? {};

        Object.setPrototypeOf(this, ApiError.prototype);
    }
}