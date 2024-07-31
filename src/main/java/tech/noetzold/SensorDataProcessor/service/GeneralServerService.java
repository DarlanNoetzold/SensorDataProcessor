package tech.noetzold.SensorDataProcessor.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;

import java.util.List;

@Service
public class GeneralServerService {

    private static final Logger logger = LoggerFactory.getLogger(GeneralServerService.class);

    @Value("${general.server.url}")
    private String generalServerUrl;

    public void sendDataToGeneralServer(List<SensorDataRaw> data) {
        RestTemplate restTemplate = new RestTemplate();
        for (SensorDataRaw sensorData : data) {
            ResponseEntity<String> response = restTemplate.postForEntity(generalServerUrl, sensorData, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Data sent to general server: {}", sensorData);
            } else {
                logger.error("Failed to send data to general server: {}", sensorData);
            }
        }
    }
}
