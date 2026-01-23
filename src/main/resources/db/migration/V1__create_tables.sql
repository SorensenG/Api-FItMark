-- Se você usar gen_random_uuid():
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE users (
                       id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username          VARCHAR(120) NOT NULL,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE workouts (
                          id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id    UUID         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
                          title      VARCHAR(120) NOT NULL,
                          notes      TEXT,
                          created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE exercises (
                           id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           workout_id UUID         NOT NULL REFERENCES workouts (id) ON DELETE CASCADE,
                           name       VARCHAR(120) NOT NULL,
                           sets       INT          NOT NULL ,
                           reps       INT          NOT NULL ,
                           weight     NUMERIC(6,2) CHECK (weight >= 0),
                           rest_seconds INT        NOT NULL DEFAULT 0 CHECK (rest_seconds >= 0),
                           created_at TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_workouts_user_id ON workouts (user_id);
CREATE INDEX idx_exercises_workout_id ON exercises (workout_id);
