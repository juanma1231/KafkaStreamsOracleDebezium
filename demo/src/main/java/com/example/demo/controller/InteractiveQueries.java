package com.example.demo.controller;

import com.example.demo.dto.ComunicacionesJoinEstado;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rocksdb")
public class InteractiveQueries {
    private final StreamsBuilderFactoryBean factoryBean;

    @Autowired
    public InteractiveQueries(StreamsBuilderFactoryBean factoryBean) {
        this.factoryBean = factoryBean;
    }

    @GetMapping("/prefix")
    public String findByPrefix(@RequestParam String prefix, @RequestParam String store){
        try {
            KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
            StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesJoinEstado>> queryParameters= StoreQueryParameters.fromNameAndType(store, QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,ComunicacionesJoinEstado> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
            ObjectMapper objectMapper= new ObjectMapper();
            String data = objectMapper.writeValueAsString(keyValueStoreGlobalKTable.prefixScan(prefix.concat("-"), Serdes.String().serializer()));
            //vlaidacion en el foreach que esto tenga la clave completada, hacer un foreach de ese key value iterator, este me entrega una ket especifica, de una uno de esos regristros le hago un split, la primer posicion
            //SPLIT POR - y la comparo con el prefix la primera parte del array, 
            return data;

        } catch (Exception e) {
            return "Error realizando la peticion " + e.getMessage();
        }
    }
    @GetMapping("/get")
    public String findById(@RequestParam String id, @RequestParam String store){
        try {
            KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
            StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesJoinEstado>> queryParameters= StoreQueryParameters.fromNameAndType(store, QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,ComunicacionesJoinEstado> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
            ObjectMapper objectMapper= new ObjectMapper();
            String data = objectMapper.writeValueAsString(keyValueStoreGlobalKTable.get(id));
            return data;
        } catch (Exception e) {
            return "Error relizando la peticion " + e.getMessage();
        }
    }
    @GetMapping("/all")
    public String getAll(@RequestParam String store){
        try {
            KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
            StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesJoinEstado>> queryParameters= StoreQueryParameters.fromNameAndType(store, QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,ComunicacionesJoinEstado> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
            ObjectMapper objectMapper= new ObjectMapper();
            String data = objectMapper.writeValueAsString(keyValueStoreGlobalKTable.all());
            return data;
        } catch (Exception e) {
            return "Error relizando la peticion " + e.getMessage();
        }

    }



}
