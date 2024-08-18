package tech.noetzold.SensorDataProcessor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.noetzold.SensorDataProcessor.model.Metrics;
import tech.noetzold.SensorDataProcessor.repository.MetricsRepository;

import java.util.List;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    @Autowired
    private MetricsRepository metricsRepository;

    @GetMapping
    public List<Metrics> getAllMetrics() {
        List<Metrics> metricsList = metricsRepository.findAll();

        metricsRepository.deleteAll();

        return metricsList;
    }

    @GetMapping("/latest")
    public Metrics getLatestMetrics() {
        Metrics latestMetrics = metricsRepository.findTopByOrderByIdDesc();

        if (latestMetrics != null) {
            metricsRepository.delete(latestMetrics);
        }

        return latestMetrics;
    }
}