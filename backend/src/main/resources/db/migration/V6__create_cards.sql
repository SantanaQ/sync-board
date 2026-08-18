CREATE TABLE cards (
    id UUID PRIMARY KEY,
    column_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    position INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,

    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    FOREIGN KEY (column_id) REFERENCES board_columns(id)
);

CREATE INDEX idx_cards_column_position
    ON cards(column_id, position);