import {get, post} from "./apiClient.ts";
import type {BoardListResponse, BoardResponse, CreateBoardRequest} from "./types.ts";

export const getBoard =  async (projectId: string, boardId: string) =>
    get<BoardResponse>("/projects/" + projectId + "/boards/" + boardId);

export const getBoards = async (projectId: string) =>
    get<BoardListResponse[]>("/projects/" + projectId + "/boards");

export const createBoard = async (projectId: string, request: CreateBoardRequest) =>
    post<BoardResponse>(`/projects/${projectId}/boards`, request);

