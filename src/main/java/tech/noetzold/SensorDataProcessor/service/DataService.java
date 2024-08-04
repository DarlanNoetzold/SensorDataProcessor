package tech.noetzold.SensorDataProcessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(DataService.class);

    @Autowired
    private SensorDataRawRepository sensorDataRawRepository;

    @Autowired
    private SensorDataProcessedRepository sensorDataProcessedRepository;

    static {
        try {
            System.loadLibrary("libdata_filter");
            System.loadLibrary("libdata_compression");
            System.loadLibrary("libdata_aggregation");
            logger.info("Libraries loaded successfully.");
        } catch (UnsatisfiedLinkError e) {
            logger.error("Failed to load libraries.", e);
        }
    }

    public native double[] filterData(double[] data);

    public native double[] compressData(double[] data);

    public native double[] aggregateData(double[] data);

    public List<SensorDataRaw> dataFilter(SensorDataRaw sensorDataRaw) {
        List<SensorDataRaw> data = sensorDataRawRepository.findAll();
        double[] rawData = data.stream().mapToDouble(SensorDataRaw::getValue).toArray();
        double[] filteredData = filterData(rawData);
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
        double[] compressedData = compressData(rawData);
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
        double[] aggregatedData = aggregateData(rawData);
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