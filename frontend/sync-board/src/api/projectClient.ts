import type {CreateProjectRequest, ProjectListResponse, ProjectResponse, UpdateProjectRequest} from "./types";
import {get, del, post, put} from "./apiClient.ts";

export const getProject =  async (id: string) =>
    get<ProjectResponse>(`/projects/${id}`);

export const getProjects = async () =>
    get<ProjectListResponse[]>(`/projects`);

export const createProject = async (request: CreateProjectRequest) =>
    post<ProjectResponse>(`/projects`, request);

export const updateProject = async (id: string, request: UpdateProjectRequest) =>
    put<ProjectResponse>(`/projects/${id}`, request);

export const deleteProject = async (id: string) =>
    del(`/projects/${id}`);