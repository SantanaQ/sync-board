CREATE TABLE boards (
    id UUID PRIMARY KEY,
    project_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);

CREATE INDEX idx_boards_project_id
    ON boards(project_id);