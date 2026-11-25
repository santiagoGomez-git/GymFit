package com.gymfit;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 100)
    public String routineName;

    public LocalDate date;
    public int minutes;

    @Column(length = 1000)
    public String notes;

    // --- NUEVOS CAMPOS PARA SEGUIMIENTO DE PROGRESO DE CARGAS ---
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id")
    public Exercise exercise;

    public Double weightKg;
    public Integer setsCompleted;
    public Integer repsCompleted;

    public WorkoutLog() {}
}