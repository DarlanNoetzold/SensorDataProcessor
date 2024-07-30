package tech.noetzold.SensorDataProcessor.service;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tech.noetzold.SensorDataProcessor.model.SensorData;
import tech.noetzold.SensorDataProcessor.repository.SensorDataRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MqttService implements MqttCallback {

    @Autowired
    private SensorDataRepository sensorDataRepository;

    @Autowired
    private DataService dataService;

    private MqttClient client;
    private final String GENERAL_SERVER_URL = "http://general-server:8081/api/data_receiver"; // URL do servidor geral

    @PostConstruct
    public void init() {
        connectToMqttBroker();
    }

    private void connectToMqttBroker() {
        try {
            client = new MqttClient("tcp://mosquitto:1883", MqttClient.generateClientId());
            client.setCallback(this);
            client.connect();
            client.subscribe("sensor/#");
            System.out.println("Connected to MQTT broker and subscribed to topics.");
        } catch (MqttException e) {
            e.printStackTrace();
            reconnectToMqttBroker();
        }
    }

    private void reconnectToMqttBroker() {
        while (!client.isConnected()) {
            try {
                System.out.println("Attempting to reconnect to MQTT broker...");
                client.connect();
                client.subscribe("sensor/#");
                System.out.println("Reconnected to MQTT broker and subscribed to topics.");
            } catch (MqttException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(5000); // Esperar 5 segundos antes de tentar reconectar
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.out.println("Connection lost, attempting to reconnect...");
        reconnectToMqttBroker();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String sensorType = topic.split("/")[1];
        double value = Double.parseDouble(new String(message.getPayload()));
        String coordinates = "0,0"; // Exemplo de coordenadas
        SensorData sensorData = new SensorData();
        sensorData.setSensorType(sensorType);
        sensorData.setValue(value);
        sensorData.setCoordinates(coordinates);
        sensorData.setTimestamp(LocalDateTime.now());

        // Salvar dados brutos no banco de dados
        sensorDataRepository.save(sensorData);
        System.out.println("Message arrived and saved: " + sensorType + " - " + value);

        // Aplicar tratamentos de dados (filtros, compressão, agregação, etc.)
        List<SensorData> filteredData = dataService.dataFilter(sensorData);
        List<SensorData> compressedData = dataService.dataCompression(filteredData);
        List<SensorData> aggregatedData = dataService.dataAggregation(compressedData);

        // Enviar dados processados para o servidor geral
        sendDataToGeneralServer(aggregatedData);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            System.out.println("Delivery complete for message: " + token.getMessage());
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sendDataToGeneralServer(List<SensorData> data) {
        RestTemplate restTemplate = new RestTemplate();
        for (SensorData sensorData : data) {
            ResponseEntity<String> response = restTemplate.postForEntity(GENERAL_SERVER_URL, sensorData, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Data sent to general server: " + sensorData);
            } else {
                System.out.println("Failed to send data to general server: " + sensorData);
            }
        }
    }
}
