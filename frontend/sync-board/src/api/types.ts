export type UserResponse = {
    id: string;
    displayName: string;
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

export type BoardResponse = {
    id: string;
    name: string;
    createdAt: string;
    updatedAt: string;
}

export type BoardListResponse = {
    id: string;
    name: string;
    createdAt: string;
    updatedAt: string;
}

export type CreateBoardRequest = {
    name: string;
}