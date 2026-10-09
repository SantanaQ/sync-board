import {del, get, post, put} from "./apiClient.ts";
import type {
    BoardColumnResponse,
    CreateBoardColumnRequest,
    UpdateBoardColumnRequest,
} from "./types.ts";

export const getColumns = async (projectId: string, boardId: string) =>
    get<BoardColumnResponse[]>(`/projects/${projectId}/boards/${boardId}/columns`);

export const getColumn = async (projectId: string, boardId: string, columnId: string) =>
    get<BoardColumnResponse>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}`);

export const createColumn = async (projectId: string, boardId: string, request : CreateBoardColumnRequest) =>
    post<BoardColumnResponse>(`/projects/${projectId}/boards/${boardId}/columns`, request);

export const updateColumn = async (projectId: string, boardId: string, columnId: string, request : UpdateBoardColumnRequest) =>
    put<BoardColumnResponse>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}`, request);

export const deleteColumn = async (projectId: string, boardId: string, columnId: string) =>
    del(`/projects/${projectId}/boards/${boardId}/columns/${columnId}`);