-- 0) garantir que valores nulos virem 0 (antes do rename)
UPDATE exercises
SET reps = 0
WHERE reps IS NULL;

-- 1) renomear coluna reps -> last_top_set_reps
ALTER TABLE exercises
    RENAME COLUMN reps TO last_top_set_reps;

-- 2) coluna deixa de ser obrigatória
ALTER TABLE exercises
    ALTER COLUMN last_top_set_reps DROP NOT NULL;

-- 3) quando NÃO enviar, vira 0
ALTER TABLE exercises
    ALTER COLUMN last_top_set_reps SET DEFAULT 0;

-- 4) ajustar o CHECK (antes era reps > 0)
ALTER TABLE exercises
DROP CONSTRAINT IF EXISTS exercises_reps_check;

ALTER TABLE exercises
    ADD CONSTRAINT exercises_last_top_set_reps_check
        CHECK (last_top_set_reps IS NULL OR last_top_set_reps >= 0);
