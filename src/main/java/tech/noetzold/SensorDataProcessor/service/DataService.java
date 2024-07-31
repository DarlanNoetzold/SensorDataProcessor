package tech.noetzold.SensorDataProcessor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.noetzold.SensorDataProcessor.model.SensorDataProcessed;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;
import tech.noetzold.SensorDataProcessor.repository.SensorDataProcessedRepository;
import tech.noetzold.SensorDataProcessor.repository.SensorDataRawRepository;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {

    @Autowired
    private SensorDataRawRepository sensorDataRawRepository;

    @Autowired
    private SensorDataProcessedRepository sensorDataProcessedRepository;

    public List<SensorDataRaw> dataFilter(SensorDataRaw sensorDataRaw) {
        // Implementar lógica de filtragem simples
        List<SensorDataRaw> data = sensorDataRawRepository.findAll();
        // Filtrar dados repetidos ou menos importantes
        return data.stream().filter(d -> !d.equals(sensorDataRaw)).collect(Collectors.toList());
    }

    public List<SensorDataRaw> dataCompression(List<SensorDataRaw> data) {
        // Implementar lógica de compressão simples
        return data; // Exemplo: não faz nada
    }

    public List<SensorDataRaw> dataAggregation(List<SensorDataRaw> data) {
        // Implementar lógica de agregação simples
        return data; // Exemplo: não faz nada
    }

    public void saveRawData(SensorDataRaw sensorDataRaw) {
        sensorDataRawRepository.save(sensorDataRaw);
    }

    public void saveProcessedData(List<SensorDataRaw> data) {
        List<SensorDataProcessed> processedData = data.stream().map(d -> {
            SensorDataProcessed processed = new SensorDataProcessed();
            processed.setSensorType(d.getSensorType());
            processed.setValue(d.getValue());
            processed.setCoordinates(d.getCoordinates());
            processed.setTimestamp(d.getTimestamp());
            return processed;
        }).collect(Collectors.toList());
        sensorDataProcessedRepository.saveAll(processedData);
    }
}