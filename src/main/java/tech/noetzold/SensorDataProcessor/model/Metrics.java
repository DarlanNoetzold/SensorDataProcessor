package tech.noetzold.SensorDataProcessor.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.Map;

@Entity
@Data
public class Metrics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double cpuUsage;
    private long memoryUsage;
    private int threadCount;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "sensor_variance", joinColumns = @JoinColumn(name = "metrics_id"))
    @MapKeyColumn(name = "sensor_type")
    @Column(name = "variance")
    private Map<String, Double> varianceMap;

    private long totalDataReceived;
    private long totalDataFiltered;
    private long totalDataCompressed;
    private long totalDataAggregated;
    private long totalDataAfterHeuristics;
    private long errorCount;
}