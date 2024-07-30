package tech.noetzold.SensorDataProcessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.noetzold.SensorDataProcessor.model.SensorData;

public interface SensorDataRepository extends JpaRepository<SensorData, Long> {
}
