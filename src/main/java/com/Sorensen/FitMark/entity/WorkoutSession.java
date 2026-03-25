package com.Sorensen.FitMark.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "workout_sessions")
public class WorkoutSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    // sessão pertence ao usuário
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"workouts", "workoutSessions"}) // evita loop se você serializar entidade
    private User user;

    // sessão aponta pro template (Workout)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    @JsonIgnoreProperties({"exercises", "workoutSessions", "user"})
    private Workout workout;

    @Column(name = "workout_date", nullable = false)
    private OffsetDateTime workoutDate;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(nullable = false)
    private Boolean completed = false;

    @Column(nullable = false)
    private Boolean abandoned = false;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    // lista de séries realizadas nessa sessão (opcional, mas útil)
    @OneToMany(mappedBy = "workoutSession", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"workoutSession"})
    private List<SetLog> sets = new ArrayList<>();
}
