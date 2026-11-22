CREATE TABLE outbox_events (
    event_id UUID PRIMARY KEY,
    aggregate_id UUID NOT NULL,
    event_type VARCHAR(120) NOT NULL,
    character_name VARCHAR(100) NOT NULL,
    character_role VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL,
    schema_version INTEGER NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_events_pending
    ON outbox_events (created_at)
    WHERE published_at IS NULL;

CREATE TABLE processed_events (
    event_id UUID PRIMARY KEY,
    handled_at TIMESTAMPTZ NOT NULL
);

CREATE TABLE character_created_audit (
    event_id UUID PRIMARY KEY REFERENCES processed_events (event_id),
    character_id UUID NOT NULL,
    character_name VARCHAR(100) NOT NULL,
    character_role VARCHAR(50) NOT NULL,
    occurred_at TIMESTAMPTZ NOT NULL
);
