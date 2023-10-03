package com.example.demo.controller;

import com.example.demo.dto.ComunicacionesJoinEstado;
import com.example.demo.utils.UtiilsDate;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@CrossOrigin(origins = {"http://localhost:4200/", "http://localhost:8080"})
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
    @GetMapping("/filer")
    public  List<ComunicacionesJoinEstado> filterByDate(@RequestParam String store, @RequestParam String date){
        try {
            KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
            StoreQueryParameters<ReadOnlyKeyValueStore<String, String>> queryParameters= StoreQueryParameters.fromNameAndType("indexComunicacionesEstado-store", QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,String> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
            ObjectMapper objectMapper= new ObjectMapper();

            Long dateToEpoch = UtiilsDate.changeToEpoch(date);

            KeyValueIterator<String,String> keyValueIterator= keyValueStoreGlobalKTable.prefixScan(dateToEpoch + "|",Serdes.String().serializer());


            StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesJoinEstado>> queryParametersIndex= StoreQueryParameters.fromNameAndType("cumunicaciones-join-store", QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,ComunicacionesJoinEstado> keyValueStoreGlobalKTableIndex = kafkaStreams.store(queryParametersIndex);
            List<ComunicacionesJoinEstado> indexList = new ArrayList<>();
            keyValueIterator.forEachRemaining((iterator)->{

                indexList.add(keyValueStoreGlobalKTableIndex.get(iterator.value));

            });
            return indexList;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @GetMapping("/filer/range")
    public  List<ComunicacionesJoinEstado> filterByRangeDate(@RequestParam String store, @RequestParam String date, @RequestParam String secondDate){
        try {
            KafkaStreams kafkaStreams = factoryBean.getKafkaStreams();
            StoreQueryParameters<ReadOnlyKeyValueStore<String, String>> queryParameters= StoreQueryParameters.fromNameAndType("indexComunicacionesEstado-store", QueryableStoreTypes.keyValueStore());
            ReadOnlyKeyValueStore<String,String> keyValueStoreGlobalKTable = kafkaStreams.store(queryParameters);
            ObjectMapper objectMapper= new ObjectMapper();

            Long menor = UtiilsDate.changeToEpoch(date);
            Long mayor= UtiilsDate.changeToEpoch(secondDate);

            List<ComunicacionesJoinEstado> indexList = new ArrayList<>();
            for(Long i = menor; i<=mayor; i++){
                KeyValueIterator<String,String> keyValueIterator= keyValueStoreGlobalKTable.prefixScan(i + "|",Serdes.String().serializer());
                StoreQueryParameters<ReadOnlyKeyValueStore<String, ComunicacionesJoinEstado>> queryParametersIndex= StoreQueryParameters.fromNameAndType("cumunicaciones-join-store", QueryableStoreTypes.keyValueStore());
                ReadOnlyKeyValueStore<String,ComunicacionesJoinEstado> keyValueStoreGlobalKTableIndex = kafkaStreams.store(queryParametersIndex);
                keyValueIterator.forEachRemaining((iterator)->{

                    indexList.add(keyValueStoreGlobalKTableIndex.get(iterator.value));

                });
            }





            return indexList;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
