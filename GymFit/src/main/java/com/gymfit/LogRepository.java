package com.gymfit;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LogRepository extends JpaRepository<WorkoutLog, Long> {
    List<WorkoutLog> findByExerciseIdAndWeightKgIsNotNullOrderByDateAscIdAsc(Long exerciseId);
}