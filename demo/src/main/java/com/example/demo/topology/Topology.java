package com.example.demo.topology;
import com.example.demo.dto.Comunicaciones;
import com.example.demo.dto.ComunicacionesEstado;
import com.example.demo.dto.ComunicacionesJoinEstado;
import com.example.demo.serder.SerdeFactory;
import com.example.demo.store.ComunicacionesEstadoNoEncontrado;
import com.example.demo.store.ComunicacionesJoinStore;
import com.example.demo.store.ComunicaionesStore;
import com.example.demo.store.IndexComunicacionesEstado;
import com.example.demo.utils.UtiilsDate;
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

    @Value(value ="${topic.index.comunicaciones.estado}")
    String indexComunicacionesEstado;

    @Value(value = "${topic.resultado.comunicaciones.join.estado}")
    String joinTopic;

    @Value(value = "${topic.comunicacion.no.encontrado}")
    String comunicacionNoTopic;


    @Autowired
    void  buildPipeline(StreamsBuilder builder){

        GlobalKTable<String, Comunicaciones> storeComunicaciones= builder.globalTable(comunicacionesTopic, Materialized.<String,Comunicaciones, KeyValueStore<Bytes,byte[]>>as(ComunicaionesStore.STORE_NAME)
                .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                .withValueSerde(new SerdeFactory<Comunicaciones>().getSerde(Comunicaciones.class)));

        GlobalKTable<String, ComunicacionesJoinEstado> storejoined= builder.globalTable(joinTopic, Materialized.<String,ComunicacionesJoinEstado, KeyValueStore<Bytes,byte[]>>as(ComunicacionesJoinStore.STORE_NAME)
                .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                .withValueSerde(new SerdeFactory<ComunicacionesJoinEstado>().getSerde(ComunicacionesJoinEstado.class)));

        KStream<String, ComunicacionesEstado> estadoKStream = builder.stream(estadoTopic,
                Consumed.with(Serdes.String(),new SerdeFactory<ComunicacionesEstado>().getSerde(ComunicacionesEstado.class)));

        GlobalKTable<String, String> storeInexComunicacionesEstado= builder.globalTable(indexComunicacionesEstado, Materialized.<String,String, KeyValueStore<Bytes,byte[]>>as(IndexComunicacionesEstado.STORE_NAME)
                .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                .withValueSerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer())));

        GlobalKTable<String, ComunicacionesEstado> storeComunicacionesNoEcontrado= builder.globalTable(comunicacionNoTopic, Materialized.<String,ComunicacionesEstado, KeyValueStore<Bytes,byte[]>>as(ComunicacionesEstadoNoEncontrado.STORE_NAME)
                .withKeySerde(Serdes.serdeFrom(Serdes.String().serializer(), Serdes.String().deserializer()))
                .withValueSerde(new SerdeFactory<ComunicacionesEstado>().getSerde(ComunicacionesEstado.class)));


        System.out.println("este es la fecha como llega a java");
        estadoKStream.foreach(((key, value) -> System.out.println(value.getCreateDate())));
//flujo aparte que valide, aplicar una branch, mejor con un outer join y hacer un branch, validar con el branch, si es true lo envia a un topic,

        KStream<String,ComunicacionesJoinEstado> joined = estadoKStream.leftJoin(storeComunicaciones,
                (leftkey,leftvalue)->leftkey,
                (leftvalue,rightvalue)->{
                    if(rightvalue==null){
                        ComunicacionesJoinEstado comunicacionesJoinEstado = new ComunicacionesJoinEstado();
                        comunicacionesJoinEstado.setCreateDateComunicaciones("");
                        comunicacionesJoinEstado.setEstado(leftvalue.getEstado());
                        comunicacionesJoinEstado.setCreateDateEstado(leftvalue.getCreateDate());
                        comunicacionesJoinEstado.setDataComunicaciones(null);
                        comunicacionesJoinEstado.setDataEstado(leftvalue.getData());
                        comunicacionesJoinEstado.setEvento(null);
                        comunicacionesJoinEstado.setUniqueId(leftvalue.getUniqueId());
                        comunicacionesJoinEstado.setUuid(leftvalue.getUuid());
                        return comunicacionesJoinEstado;
                    }else {
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
                    }
                });
        KStream<String,ComunicacionesJoinEstado> [] branches = joined.branch((key, value) -> value.getDataComunicaciones()==null,
                ((key, value) -> value.getDataComunicaciones() != null));
        KStream<String,ComunicacionesEstado> noEncontrado = branches[0].map(((key, value) -> {
            ComunicacionesEstado comunicacionesEstado = new ComunicacionesEstado();
            comunicacionesEstado.setData(value.getDataEstado());
            comunicacionesEstado.setUnique_id(value.getUniqueId());
            comunicacionesEstado.setEstado(value.getEstado());
            comunicacionesEstado.setCreateDate(value.getCreateDateEstado());
            comunicacionesEstado.setUuid(value.getUuid());
            KeyValue keyValue = new KeyValue(key,comunicacionesEstado);
            return keyValue;
        }));

        noEncontrado.map(((key, value) -> KeyValue.pair(value.getUuid().concat("|").concat(value.getUniqueId()),value))).to(comunicacionNoTopic,Produced.with(Serdes.String(), new SerdeFactory<ComunicacionesEstado>().getSerde(ComunicacionesEstado.class)));

        KStream<String,ComunicacionesJoinEstado> comunicacionesJoinEstado=branches[1].map(((key, value) -> KeyValue.pair(value.getUuid()+"|"+value.getUniqueId(),value)));

        comunicacionesJoinEstado.to(joinTopic, Produced.with(Serdes.String(), new SerdeFactory<ComunicacionesJoinEstado>().getSerde(ComunicacionesJoinEstado.class)));

        comunicacionesJoinEstado.map(((key, value) -> {
             Long dateEpoch = UtiilsDate.changeToEpoch(value.getCreateDateEstado());
             return KeyValue.pair(dateEpoch + "|" + key,key);
        })).to(indexComunicacionesEstado,Produced.with(Serdes.String(),Serdes.String()));

        comunicacionesJoinEstado.map(((key, value) -> KeyValue.pair(value.getEstado().concat("|").concat(key),key)));


                //Para las fechas con -


    }



}
