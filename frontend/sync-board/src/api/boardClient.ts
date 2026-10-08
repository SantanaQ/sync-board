import {del, get, post, put} from "./apiClient.ts";
import type {BoardListResponse, BoardResponse, CreateBoardRequest, UpdateBoardRequest} from "./types.ts";

export const getBoard =  async (projectId: string, boardId: string) =>
    get<BoardResponse>("/projects/" + projectId + "/boards/" + boardId);

export const getBoards = async (projectId: string) =>
    get<BoardListResponse[]>("/projects/" + projectId + "/boards");

export const createBoard = async (projectId: string, request: CreateBoardRequest) =>
    post<BoardResponse>(`/projects/${projectId}/boards`, request);

export const updateBoard = async (projectId: string, boardId: string, request: UpdateBoardRequest) =>
    put<BoardResponse>(`/projects/${projectId}/boards/${boardId}`, request);

export const deleteBoard = async (projectId: string, boardId: string) =>
    del(`/projects/${projectId}/boards/${boardId}`);

