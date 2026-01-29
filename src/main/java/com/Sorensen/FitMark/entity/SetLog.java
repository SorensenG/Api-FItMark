package com.Sorensen.FitMark.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Set_Logs")
public class SetLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    // série pertence a uma sessão (dia)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_session_id", nullable = false)
    @JsonIgnoreProperties({"sets"})
    private WorkoutSession workoutSession;

    // série referencia qual exercício template foi executado (seu Exercise)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercise_id", nullable = false)
    @JsonIgnoreProperties({"workout", "setLogs"})
    private Exercise exercise;

    @Column(name = "set_number", nullable = false)
    private Integer setNumber;

    @Column(nullable = false)
    private Integer reps;

    @Enumerated(EnumType.STRING)
    @Column(name = "set_type", nullable = false, length = 30)
    private SetType setType;

    @Column(precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(name = "rest_seconds", nullable = false)
    private Integer restSeconds = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
