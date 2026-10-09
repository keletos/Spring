CREATE TABLE cyberpunk_character (
    id               UUID PRIMARY KEY,
    name             VARCHAR(100) NOT NULL,
    role             VARCHAR(50) NOT NULL,
    -- Stats
    level            INT NOT NULL DEFAULT 1,
    eddies           INT NOT NULL DEFAULT 2550,

    max_health       INT NOT NULL,
    max_humanity     INT NOT NULL,

    current_health   INT NOT NULL,
    current_humanity INT NOT NULL,
    current_luck_points INT NOT NULL,
    
    -- Characteristics
    intelligence     INT NOT NULL,
    reflex           INT NOT NULL,
    dexterity        INT NOT NULL,
    technology       INT NOT NULL,
    cool             INT NOT NULL,
    will             INT NOT NULL,
    movement         INT NOT NULL,
    body             INT NOT NULL,
    empathy          INT NOT NULL,
    luck             INT NOT NULL,
    
    -- Обязательное поле для контроля конкурентности
    version          BIGINT NOT NULL DEFAULT 0
);
