-- V3__add_position_to_exercises.sql

ALTER TABLE exercises
    ADD COLUMN position INT NOT NULL;

ALTER TABLE exercises
    ALTER COLUMN position SET DEFAULT 0;

ALTER TABLE exercises
    ADD CONSTRAINT exercises_position_check
        CHECK (position >= 0);

CREATE INDEX idx_exercises_workout_position
    ON exercises(workout_id, position);