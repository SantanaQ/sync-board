CREATE TABLE board_columns(
    id UUID PRIMARY KEY,
    board_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    position NUMERIC(30, 10) NOT NULL,

    FOREIGN KEY (board_id) REFERENCES boards(id) ON DELETE CASCADE
);

CREATE INDEX idx_board_columns_board_id
    ON board_columns(board_id);