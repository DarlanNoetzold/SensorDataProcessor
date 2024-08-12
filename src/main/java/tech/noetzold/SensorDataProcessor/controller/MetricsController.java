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
        return metricsRepository.findAll();
    }

    @GetMapping("/latest")
    public Metrics getLatestMetrics() {
        return metricsRepository.findTopByOrderByIdDesc();
    }
}
