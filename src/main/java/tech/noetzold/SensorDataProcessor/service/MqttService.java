package tech.noetzold.SensorDataProcessor.service;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tech.noetzold.SensorDataProcessor.model.SensorDataRaw;

import java.time.LocalDateTime;

@Service
public class MqttService implements MqttCallback {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Value("${mqtt.broker.url}")
    private String brokerUrl;

    @Autowired
    private DataService dataService;

    private MqttClient client;

    @PostConstruct
    public void init() {
        connectToMqttBroker();
    }

    private void connectToMqttBroker() {
        try {
            client = new MqttClient(brokerUrl, MqttClient.generateClientId());
            client.setCallback(this);
            client.connect();
            String[] topics = {"zigbee2mqtt/temperature", "zigbee2mqtt/humidity", "zigbee2mqtt/motion",
                    "zigbee2mqtt/presence", "zigbee2mqtt/co2", "zigbee2mqtt/energy", "zigbee2mqtt/water_flow"};
            client.subscribe(topics);
            logger.info("Connected to MQTT broker at {} and subscribed to topics.", brokerUrl);
        } catch (MqttException e) {
            logger.error("Failed to connect and subscribe to MQTT broker at {}. Attempting to reconnect...", brokerUrl, e);
            reconnectToMqttBroker();
        }
    }

    private void reconnectToMqttBroker() {
        while (!client.isConnected()) {
            try {
                logger.info("Attempting to reconnect to MQTT broker...");
                client.connect();
                String[] topics = {"zigbee2mqtt/temperature", "zigbee2mqtt/humidity", "zigbee2mqtt/motion",
                        "zigbee2mqtt/presence", "zigbee2mqtt/co2", "zigbee2mqtt/energy", "zigbee2mqtt/water_flow"};
                client.subscribe(topics);
                logger.info("Reconnected to MQTT broker and subscribed to topics.");
            } catch (MqttException e) {
                logger.error("Failed to reconnect to MQTT broker. Retrying in 5 seconds...", e);
                try {
                    Thread.sleep(5000); // Esperar 5 segundos antes de tentar reconectar
                } catch (InterruptedException ex) {
                    logger.error("Reconnection attempt interrupted", ex);
                }
            }
        }
    }

    @Override
    public void connectionLost(Throwable cause) {
        logger.warn("Connection lost to MQTT broker. Attempting to reconnect...", cause);
        reconnectToMqttBroker();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        logger.debug("Message arrived on topic: {}, message: {}", topic, new String(message.getPayload()));

        String sensorType = topic.split("/")[1];
        double value = Double.parseDouble(new String(message.getPayload()));
        String coordinates = "0,0"; // Exemplo de coordenadas
        SensorDataRaw sensorDataRaw = new SensorDataRaw();
        sensorDataRaw.setSensorType(sensorType);
        sensorDataRaw.setValue(value);
        sensorDataRaw.setCoordinates(coordinates);
        sensorDataRaw.setTimestamp(LocalDateTime.now());

        // Processar e salvar os dados recebidos
        dataService.saveRawData(sensorDataRaw);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            logger.debug("Delivery complete for message: {}", token.getMessage());
        } catch (MqttException e) {
            logger.error("Failed to get delivered message", e);
        }
    }
}
