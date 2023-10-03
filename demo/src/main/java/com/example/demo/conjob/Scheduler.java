package com.example.demo.conjob;

import com.example.demo.dto.Comunicaciones;
import com.example.demo.dto.ComunicacionesEstado;
import com.example.demo.store.ComunicacionesJoinStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.kafka.support.KafkaHeaders;



import org.springframework.scheduling.annotation.Scheduled;

@Component
public class Scheduler {

    @Value("${topic.customers.comunicaciones}")
    String topicCominicaciones;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");


    private static final Logger LOGGER = LogManager.getLogger(Scheduler.class);

    private final StreamsBuilderFactoryBean factoryBean;

    private List<String> keys = new LinkedList<>();

    private List<String> values = new LinkedList<>();



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
                Date currentDate = new Date();
                long diferenciaMilisegundos = localDate.getTime() - currentDate.getTime();
                long diferenciaHoras = TimeUnit.MILLISECONDS.toHours(diferenciaMilisegundos);
                if(diferenciaHoras > 24){
                    sendMessage(topic, data.key, null);
                }
            }catch (Exception exception){
                sendMessage(topic, data.key, null);
            }
        });
        contentOfGlobalKTable.close();    }
    @Scheduled(fixedDelay = 1000*60)
    private void retryTopic() throws JsonProcessingException {
        KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
        StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesEstado>> queryParameters = StoreQueryParameters.fromNameAndType("cumunicaciones-estado-no-encontrado-store", QueryableStoreTypes.keyValueStore());
        ReadOnlyKeyValueStore<String, ComunicacionesEstado> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
        KeyValueIterator<String, ComunicacionesEstado> contentOfGlobalKTable = keyValueStoreGlobalKTable.all();
        ObjectMapper mapper = new ObjectMapper();
        String data = mapper.writeValueAsString(keyValueStoreGlobalKTable.all());
        System.out.println(data);
        contentOfGlobalKTable.forEachRemaining(iterator->{
            try {
                String value = mapper.writeValueAsString(iterator.value);
                kafkaTemplate.send("PRUEBA.PRUEBA.ESTADO",iterator.value.getUuid(),value);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
    }
    /*@Scheduled(fixedDelay = 1000*60)
    public void shecduleTask() {
        System.out.println("se esta ejecutando");
        while(keys.size()!=0){
            kafkaTemplate.send("PRUEBA.PRUEBA.ESTADO", keys.get(0),values.get(0));
            System.out.println(keys.get(0));
            System.out.println(values.get(0));
            keys.remove(0);
            values.remove(0);
        }

    }
    @KafkaListener(topics = "comunicacion.no.encontrado", groupId = "your-consumer-group")
    public void consume(@Payload String message, @Header(KafkaHeaders.RECEIVED_KEY) String key){
        //System.out.println("mensaje: "+message);
        //System.out.println("key: "+ key);
        keys.add(key);
        values.add(message);
        System.out.println(values);
        System.out.println(keys);

        //kafkaTemplate.send("PRUEBA.PRUEBA.ESTADO",key,message);
        //kafkaTemplate.send("comunicacion.no.encontrado",key,null);
    }*/


}