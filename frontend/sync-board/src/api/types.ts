export type UserResponse = {
    id: string;
    name: string;
    email: string;
}

export type CreateProjectRequest = {
    name: string;
    description: string;
}

export type UpdateProjectRequest = {
    name: string;
    description: string;
}

export type ProjectResponse = {
    id : string;
    name: string;
    description: string;
    createdAt: string;
    updatedAt: string;
    owner: UserResponse;
    currentUserRole: string;
}

export type ProjectListResponse = {
    id: string;
    name: string;
    description: string;
    createdAt: string;
    updatedAt: string;
    currentUserRole: string;
}

