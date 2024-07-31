package tech.noetzold.SensorDataProcessor.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.noetzold.SensorDataProcessor.model.SensorDataProcessed;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;
import tech.noetzold.SensorDataProcessor.repository.SensorDataProcessedRepository;
import tech.noetzold.SensorDataProcessor.repository.SensorDataRawRepository;

import java.util.List;

@RestController
@RequestMapping("/api/query")
public class QueryController {

    @Autowired
    private SensorDataRawRepository sensorDataRawRepository;

    @Autowired
    private SensorDataProcessedRepository sensorDataProcessedRepository;

    @GetMapping("/raw")
    public List<SensorDataRaw> getAllRawData() {
        return sensorDataRawRepository.findAll();
    }

    @GetMapping("/processed")
    public List<SensorDataProcessed> getAllProcessedData() {
        return sensorDataProcessedRepository.findAll();
    }
}
