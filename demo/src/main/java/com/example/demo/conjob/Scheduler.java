package com.example.demo.conjob;

import com.example.demo.dto.Comunicaciones;
import com.example.demo.store.ComunicacionesJoinStore;
import com.fasterxml.jackson.databind.JsonNode;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import org.springframework.scheduling.annotation.Scheduled;

@Component
public class Scheduler {

    @Value("${topic.customers.comunicaciones}")
    String topicCominicaciones;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    private final StreamsBuilderFactoryBean factoryBean;

    @Autowired
    public Scheduler(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic,String key,JsonNode msg) {
        kafkaTemplate.send(topic, key,null);
    }


    @Scheduled(cron = "${cron.expression.transaccion.ventas}")
    public void cleanTransaccionesVentas() {
        try {
            Date now = new Date();
            String strDate = sdf.format(now);
            deleteTransaccionVentas(ComunicacionesJoinStore.STORE_NAME,topicCominicaciones);
            LOGGER.info("Scheduler ejecutado Transacciones Ventas: " + strDate);
        }catch (Exception exception){
            LOGGER.warn("Exepcion en Secheduler Conciliar Dian: " + exception.getMessage());
        }
    }

    private void deleteTransaccionVentas(String storage, String topic) {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        StoreQueryParameters<ReadOnlyKeyValueStore<String, Comunicaciones>> queryParameters = StoreQueryParameters.fromNameAndType(storage, QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<String, Comunicaciones> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
        KeyValueIterator<String, Comunicaciones> contentOfGlobalKTable = keyValueStoreGlobalKTable.all();
        Date localDate = new Date();
        contentOfGlobalKTable.forEachRemaining(data->{
            try {
                Date currentDate = data.value.getCreateDate();
                long diferenciaMilisegundos = localDate.getTime() - currentDate.getTime();
                long diferenciaHoras = TimeUnit.MILLISECONDS.toHours(diferenciaMilisegundos);
                if(diferenciaHoras > 24){
                    sendMessage(topic, data.key, null);
                }
            }catch (Exception exception){
                sendMessage(topic, data.key, null);
            }
        });
        contentOfGlobalKTable.close();
    }
}