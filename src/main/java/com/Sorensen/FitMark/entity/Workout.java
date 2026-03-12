package com.Sorensen.FitMark.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
@Table(name = "workouts")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    // dono do workout template
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnoreProperties({"workouts", "workoutSessions", "splits"})
    private User user;

    // divisão (PPL, Upper/Lower...) - opcional
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "split_id")
    @JsonIgnoreProperties({"user", "workouts"})
    private Split split;

    // ordenação dentro do split
    @Column(nullable = false)
    @Builder.Default
    private Integer position = 0;

    @NotNull
    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    // exercícios template desse workout (ORDENADO POR POSITION)
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("position ASC")
    @Builder.Default
    @JsonIgnoreProperties({"workout", "setLogs"})
    private List<Exercise> exercises = new ArrayList<>();

    // sessões históricas que usaram esse workout template
    @OneToMany(mappedBy = "workout", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnoreProperties({"workout", "user"})
    private List<WorkoutSession> workoutSessions = new ArrayList<>();
}
