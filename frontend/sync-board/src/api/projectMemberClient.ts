import {get, del, post, put} from "./apiClient.ts";
import type {AddMemberRequest, ProjectMemberResponse, UpdateMemberRequest} from "./types.ts";

export const getProjectMember =  async (projectId: string, userId: string) =>
    get<ProjectMemberResponse>(`/projects/${projectId}/members/${userId}`);

export const getProjectMembers = async (projectId: string) =>
    get<ProjectMemberResponse[]>(`/projects/${projectId}/members`);

export const addProjectMember = async (projectId: string, request: AddMemberRequest) =>
    post<ProjectMemberResponse>(`/projects/${projectId}/members`, request);

export const updateProjectMember = async (projectId: string, request: UpdateMemberRequest) =>
    put<ProjectMemberResponse>(`/projects/${projectId}/members`, request);

export const removeProjectMember = async (projectId: string, userId: string) =>
    del(`/projects/${projectId}/members/${userId}`);