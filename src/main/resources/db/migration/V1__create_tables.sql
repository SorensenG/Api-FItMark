-- Enable UUID generator
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- 1) USERS
CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(120) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- 2) SPLITS (divisão do treino: PPL, Upper/Lower...)
CREATE TABLE splits (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                        user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                        name VARCHAR(120) NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                        CONSTRAINT uq_splits_user_name UNIQUE (user_id, name)
);

CREATE INDEX idx_splits_user_id ON splits(user_id);

-- 3) WORKOUTS (TEMPLATE)
CREATE TABLE workouts (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- divisão opcional + ordenação
                          split_id UUID REFERENCES splits(id) ON DELETE SET NULL,
                          position INT NOT NULL DEFAULT 0 CHECK (position >= 0),

                          title VARCHAR(120) NOT NULL,
                          notes TEXT,
                          created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_workouts_user_id ON workouts(user_id);
CREATE INDEX idx_workouts_split_id ON workouts(split_id);
CREATE INDEX idx_workouts_split_pos ON workouts(split_id, position);

-- 4) EXERCISES (TEMPLATE)
CREATE TABLE exercises (
                           id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                           workout_id UUID NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,

                           name VARCHAR(120) NOT NULL,

    -- template planejado
                           sets INT NOT NULL CHECK (sets > 0),
                           reps INT NOT NULL CHECK (reps > 0),

                           weight NUMERIC(6,2) CHECK (weight >= 0),
                           rest_seconds INT NOT NULL DEFAULT 0 CHECK (rest_seconds >= 0),

    -- cache simples (opcional)
                           last_weight NUMERIC(6,2) CHECK (last_weight >= 0),

                           created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_exercises_workout_id ON exercises(workout_id);

-- 5) ENUM set type
DO $$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'set_type_enum') THEN
            CREATE TYPE set_type_enum AS ENUM (
                'WORK', 'WARMUP', 'DROP', 'FAILURE', 'BACKOFF', 'AMRAP', 'REST_PAUSE', 'SUPERSET'
                );
        END IF;
    END $$;

-- 6) WORKOUT SESSIONS (HISTÓRICO)
CREATE TABLE workout_sessions (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                  workout_id UUID NOT NULL REFERENCES workouts(id) ON DELETE CASCADE,
                                  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,

    -- “data do treino”; se você quer só a data, use DATE
                                  workout_date TIMESTAMPTZ NOT NULL DEFAULT now(),

                                  notes TEXT,
                                  completed BOOLEAN NOT NULL DEFAULT FALSE,
                                  duration_minutes INT CHECK (duration_minutes >= 0),

                                  created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_workout_sessions_user_date
    ON workout_sessions(user_id, workout_date);

CREATE INDEX idx_workout_sessions_workout_id
    ON workout_sessions(workout_id);

-- 7) SET LOGS (LOG POR SÉRIE)
CREATE TABLE set_logs (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                          workout_session_id UUID NOT NULL REFERENCES workout_sessions(id) ON DELETE CASCADE,
                          exercise_id UUID NOT NULL REFERENCES exercises(id) ON DELETE CASCADE,

                          set_number INT NOT NULL CHECK (set_number > 0),

                          reps INT NOT NULL CHECK (reps >= 0),
                          weight NUMERIC(6,2) CHECK (weight >= 0),

                          set_type set_type_enum NOT NULL,

                          rest_seconds INT NOT NULL DEFAULT 0 CHECK (rest_seconds >= 0),

                          created_at TIMESTAMPTZ NOT NULL DEFAULT now(),

    -- evita set 1 duplicado no mesmo exercício da mesma sessão
                          CONSTRAINT uq_set_logs_session_exercise_setnum
                              UNIQUE (workout_session_id, exercise_id, set_number)
);

CREATE INDEX idx_set_logs_session_id ON set_logs(workout_session_id);
CREATE INDEX idx_set_logs_exercise_id ON set_logs(exercise_id);
