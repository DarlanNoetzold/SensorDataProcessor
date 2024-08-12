package tech.noetzold.SensorDataProcessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.noetzold.SensorDataProcessor.model.Metrics;

public interface MetricsRepository extends JpaRepository<Metrics, Long> {
    Metrics findTopByOrderByIdDesc();
}
