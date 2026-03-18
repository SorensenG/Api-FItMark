package com.Sorensen.FitMark.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
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
@Table(name = "exercises")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotNull
    @Column(nullable = false, length = 120)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer sets;


    @Column(name = "last_top_set_reps") //Ultimas repetições da série mais pesada
    @Builder.Default
    private Integer lastTopSetReps = 0;

    @Column(name = "weight", precision = 6, scale = 2) //Ultimo peso utilizado
    @Builder.Default
    private BigDecimal weight = BigDecimal.ZERO;



    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "position", nullable = false)
    @Builder.Default
    private Integer position = 0;

    @JsonIgnoreProperties({"exercises", "workoutSessions", "user"})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "workout_id", nullable = false)
    private Workout workout;

    // histórico (séries) que apontam pra esse template
    @OneToMany(mappedBy = "exercise", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @JsonIgnoreProperties({"exercise"})
    private List<SetLog> setLogs = new ArrayList<>();
}
