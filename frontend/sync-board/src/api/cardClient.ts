import {del, get, post, put} from "./apiClient.ts";
import type {CardResponse, CreateCardRequest, UpdateCardRequest} from "./types.ts";

export const getCards = async (projectId: string, boardId: string, columnId: string) =>
    get<CardResponse[]>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}/cards`);

export const getCard = async (projectId: string, boardId: string, columnId: string, cardId: string) =>
    get<CardResponse>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}/cards/${cardId}`);

export const createCard = async (projectId: string, boardId: string, columnId: string, request : CreateCardRequest) =>
    post<CardResponse>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}/cards`, request);

export const updateCard = async (projectId: string, boardId: string, columnId: string, cardId: string, request : UpdateCardRequest) =>
    put<CardResponse>(`/projects/${projectId}/boards/${boardId}/columns/${columnId}/cards/${cardId}`, request);

export const deleteCard = async (projectId: string, boardId: string, columnId: string, cardId: string) =>
    del(`/projects/${projectId}/boards/${boardId}/columns/${columnId}/cards/${cardId}`);