package tech.noetzold.SensorDataProcessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.noetzold.SensorDataProcessor.model.SensorDataProcessed;

public interface SensorDataProcessedRepository extends JpaRepository<SensorDataProcessed, Long> {
}
