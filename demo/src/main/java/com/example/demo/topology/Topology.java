package com.example.demo.topology;
import com.example.demo.dto.Comunicaciones;
import com.example.demo.dto.ComunicacionesEstado;
import com.example.demo.dto.ComunicacionesJoinEstado;
import com.example.demo.serder.SerdeFactory;
import com.example.demo.store.ComunicacionesJoinStore;
import com.example.demo.store.ComunicaionesStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Topology {

    @Value(value = "${topic.customers.comunicaciones}")
    String comunicacionesTopic;

    @Value(value = "${topic.customers.estado}")
    String estadoTopic;

    @Value(value = "${topic.resultado.comunicaciones.join.estado}")
    String joinTopic;


    @Autowired
    void  buildPipeline(StreamsBuilder builder){
        try {
            GlobalKTable<String, Comunicaciones> storeComunicaciones= builder.globalTable(comunicacionesTopic, Materialized.<String,Comunicaciones, KeyValueStore<Bytes,byte[]>>as(ComunicaionesStore.STORE_NAME)
                    .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                    .withValueSerde(new SerdeFactory<Comunicaciones>().getSerde(Comunicaciones.class)));

            GlobalKTable<String, ComunicacionesJoinEstado> storejoined= builder.globalTable(joinTopic, Materialized.<String,ComunicacionesJoinEstado, KeyValueStore<Bytes,byte[]>>as(ComunicacionesJoinStore.STORE_NAME)
                    .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                    .withValueSerde(new SerdeFactory<ComunicacionesJoinEstado>().getSerde(ComunicacionesJoinEstado.class)));

            KStream<String, ComunicacionesEstado> estadoKStream = builder.stream(estadoTopic,
                    Consumed.with(Serdes.String(),new SerdeFactory<ComunicacionesEstado>().getSerde(ComunicacionesEstado.class)));

            System.out.println("este es la fecha como llega a java");
            estadoKStream.foreach(((key, value) -> System.out.println(value.getCreateDate())));


            KStream<String,ComunicacionesJoinEstado> joined = estadoKStream.join(storeComunicaciones,
                    (leftkey,leftvalue)->leftkey,
                    (leftvalue,rightvalue)->{
                        ComunicacionesJoinEstado comunicacionesJoinEstado = new ComunicacionesJoinEstado();
                        comunicacionesJoinEstado.setCreateDateComunicaciones(rightvalue.getCreateDate());
                        comunicacionesJoinEstado.setEstado(leftvalue.getEstado());
                        comunicacionesJoinEstado.setCreateDateEstado(leftvalue.getCreateDate());
                        comunicacionesJoinEstado.setDataComunicaciones(rightvalue.getData());
                        comunicacionesJoinEstado.setDataEstado(leftvalue.getData());
                        comunicacionesJoinEstado.setEvento(rightvalue.getEvento());
                        comunicacionesJoinEstado.setUniqueId(leftvalue.getUniqueId());
                        comunicacionesJoinEstado.setUuid(leftvalue.getUuid());
                        return comunicacionesJoinEstado;
                    }).map(((key, value) -> KeyValue.pair(value.getUuid() + "-" + value.getUniqueId(),value)));
            joined.to(joinTopic, Produced.with(Serdes.String(), new SerdeFactory<ComunicacionesJoinEstado>().getSerde(ComunicacionesJoinEstado.class)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
