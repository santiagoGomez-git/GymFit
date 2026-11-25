package com.gymfit;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/progreso")
public class ProgressRestController {

    private final LogRepository logs;
    private final ExerciseRepository exercises;

    // DTO inmutable (Java 23 Record) para serialización JSON limpia
    public record ProgressPointDTO(
            Long id,
            String date,
            Double weightKg,
            Integer sets,
            Integer reps,
            String notes
    ) {}

    public ProgressRestController(LogRepository logs, ExerciseRepository exercises) {
        this.logs = logs;
        this.exercises = exercises;
    }

    @GetMapping("/{exerciseId}")
    public List<ProgressPointDTO> getProgressByExercise(@PathVariable Long exerciseId) {
        if (!exercises.existsById(exerciseId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado");
        }
        return logs.findByExerciseIdAndWeightKgIsNotNullOrderByDateAscIdAsc(exerciseId).stream()
                .map(l -> new ProgressPointDTO(
                        l.id,
                        l.date != null ? l.date.toString() : "",
                        l.weightKg,
                        l.setsCompleted,
                        l.repsCompleted,
                        l.notes
                ))
                .toList();
    }

    @PostMapping
    public ProgressPointDTO addProgressRecord(
            @RequestParam Long exerciseId,
            @RequestParam LocalDate date,
            @RequestParam Double weightKg,
            @RequestParam(defaultValue = "3") Integer sets,
            @RequestParam(defaultValue = "10") Integer reps,
            @RequestParam(defaultValue = "") String notes) {

        if (weightKg <= 0 || date == null || date.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Datos de carga inválidos");
        }

        Exercise ex = exercises.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ejercicio no encontrado"));

        WorkoutLog log = new WorkoutLog();
        log.routineName = "Progreso · " + ex.name;
        log.exercise = ex;
        log.date = date;
        log.minutes = 15;
        log.weightKg = weightKg;
        log.setsCompleted = sets;
        log.repsCompleted = reps;
        log.notes = notes.strip();

        WorkoutLog saved = logs.save(log);
        return new ProgressPointDTO(saved.id, saved.date.toString(), saved.weightKg, saved.setsCompleted, saved.repsCompleted, saved.notes);
    }
}