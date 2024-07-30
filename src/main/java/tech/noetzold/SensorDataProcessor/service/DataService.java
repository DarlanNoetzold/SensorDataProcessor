package tech.noetzold.SensorDataProcessor.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.noetzold.SensorDataProcessor.model.SensorData;
import tech.noetzold.SensorDataProcessor.repository.SensorDataRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DataService {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    public List<SensorData> dataFilter(SensorData sensorData) {
        // Implementar lógica de filtragem simples
        List<SensorData> data = sensorDataRepository.findAll();
        // Filtrar dados repetidos ou menos importantes
        return data.stream().filter(d -> !d.equals(sensorData)).collect(Collectors.toList());
    }

    public List<SensorData> dataCompression(List<SensorData> data) {
        // Implementar lógica de compressão simples
        return data; // Exemplo: não faz nada
    }

    public List<SensorData> dataAggregation(List<SensorData> data) {
        // Implementar lógica de agregação simples
        return data; // Exemplo: não faz nada
    }

    public String dataValidation() {
        // Implementar lógica de validação de dados simples
        List<SensorData> data = sensorDataRepository.findAll();
        // Validar dados
        return "Data validated";
    }

    public String dataSender() {
        // Implementar lógica de envio de dados para o servidor geral
        List<SensorData> data = sensorDataRepository.findAll();
        // Enviar dados
        return "Data sent";
    }
}