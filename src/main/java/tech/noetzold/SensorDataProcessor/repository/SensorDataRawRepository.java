package tech.noetzold.SensorDataProcessor.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;

public interface SensorDataRawRepository extends JpaRepository<SensorDataRaw, Long> {
}
