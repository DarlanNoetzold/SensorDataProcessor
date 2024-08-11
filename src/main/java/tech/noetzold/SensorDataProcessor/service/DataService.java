package tech.noetzold.SensorDataProcessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tech.noetzold.SensorDataProcessor.model.SensorDataProcessed;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;
import tech.noetzold.SensorDataProcessor.repository.SensorDataProcessedRepository;
import tech.noetzold.SensorDataProcessor.repository.SensorDataRawRepository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

@Service
public class DataService {

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private SensorDataRawRepository sensorDataRawRepository;

    @Autowired
    private SensorDataProcessedRepository sensorDataProcessedRepository;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.processed-data}")
    private String topicProcessedData;

    @Value("${use.native.heuristics:false}")
    private boolean useNativeHeuristics;

    static {
        try {
            System.loadLibrary("data_filter");
            System.loadLibrary("data_compression");
            System.loadLibrary("data_aggregation");
            logger.info("Libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            logger.error("Failed to load libraries.", e);
        }
    }

    // Métodos nativos
    public native double[] filterData(double[] data);

    public native double[] compressData(double[] data);

    public native double[] aggregateData(double[] data);

    // Métodos Java
    public double[] filterDataJava(double[] data) {
        return Arrays.stream(data).distinct().filter(d -> d > 0.1).toArray();
    }

    public double[] compressDataJava(double[] data) {
        return Arrays.stream(data).map(d -> Math.round(d * 100.0) / 100.0).toArray();
    }

    public double[] aggregateDataJava(double[] data) {
        int blockSize = 5;
        int newLength = (data.length + blockSize - 1) / blockSize;
        double[] aggregated = new double[newLength];
        for (int i = 0; i < newLength; i++) {
            double sum = 0.0;
            int count = 0;
            for (int j = 0; j < blockSize && (i * blockSize + j) < data.length; j++) {
                sum += data[i * blockSize + j];
                count++;
            }
            aggregated[i] = sum / count;
        }
        return aggregated;
    }

    // Métodos para decidir se usa nativo ou Java
    public double[] filterDataNativeOrJava(double[] data) {
        if (useNativeHeuristics) {
            return filterData(data);
        } else {
            return filterDataJava(data);
        }
    }

    public double[] compressDataNativeOrJava(double[] data) {
        if (useNativeHeuristics) {
            return compressData(data);
        } else {
            return compressDataJava(data);
        }
    }

    public double[] aggregateDataNativeOrJava(double[] data) {
        if (useNativeHeuristics) {
            return aggregateData(data);
        } else {
            return aggregateDataJava(data);
        }
    }

    public List<SensorDataRaw> dataFilter(SensorDataRaw sensorDataRaw) {
        List<SensorDataRaw> data = sensorDataRawRepository.findAll();
        double[] rawData = data.stream().mapToDouble(SensorDataRaw::getValue).toArray();
        double[] filteredData = filterDataNativeOrJava(rawData);
        return data.stream().filter(d -> {
            for (double v : filteredData) {
                if (d.getValue() == v) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public List<SensorDataRaw> dataCompression(List<SensorDataRaw> data) {
        double[] rawData = data.stream().mapToDouble(SensorDataRaw::getValue).toArray();
        double[] compressedData = compressDataNativeOrJava(rawData);
        return data.stream().filter(d -> {
            for (double v : compressedData) {
                if (d.getValue() == v) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public List<SensorDataRaw> dataAggregation(List<SensorDataRaw> data) {
        double[] rawData = data.stream().mapToDouble(SensorDataRaw::getValue).toArray();
        double[] aggregatedData = aggregateDataNativeOrJava(rawData);
        return data.stream().filter(d -> {
            for (double v : aggregatedData) {
                if (d.getValue() == v) {
                    return true;
                }
            }
            return false;
        }).collect(Collectors.toList());
    }

    public void saveRawData(SensorDataRaw sensorDataRaw) {
        sensorDataRawRepository.save(sensorDataRaw);
    }

    public void saveProcessedData(List<SensorDataRaw> data) {
        List<SensorDataProcessed> processedDataList = data.stream().map(d -> {
            SensorDataProcessed processed = new SensorDataProcessed();
            processed.setSensorType(d.getSensorType());
            processed.setValue(d.getValue());
            processed.setCoordinates(d.getCoordinates());
            processed.setTimestamp(d.getTimestamp());
            return processed;
        }).collect(Collectors.toList());

        sensorDataProcessedRepository.saveAll(processedDataList);

        // Enviar dados processados para Kafka
        processedDataList.forEach(processedData -> kafkaTemplate.send(topicProcessedData, processedData));
    }
}
